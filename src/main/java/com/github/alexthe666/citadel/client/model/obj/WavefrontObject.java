package com.github.alexthe666.citadel.client.model.obj;

import com.github.alexthe666.citadel.client.model.container.ObjFace;
import com.github.alexthe666.citadel.client.model.container.ObjGroupObject;
import com.github.alexthe666.citadel.client.model.container.ObjTextureCoordinate;
import com.github.alexthe666.citadel.client.model.container.ObjVertex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Wavefront Object importer
 * Based heavily off of the specifications found at http://en.wikipedia.org/wiki/Wavefront_.obj_file
 *
 * @author cpw
 * @since 1.0.0
 */
public class WavefrontObject implements IModelObj {
    private static Pattern objVertexPattern = Pattern.compile("(v( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(v( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
    private static Pattern objVertexNormalPattern = Pattern.compile("(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
    private static Pattern objTextureCoordinatePattern = Pattern.compile("(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *\\n)|(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *$)");
    private static Pattern face_V_VT_VN_Pattern = Pattern.compile("(f( \\d+/\\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+/\\d+){3,4} *$)");
    private static Pattern face_V_VT_Pattern = Pattern.compile("(f( \\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+){3,4} *$)");
    private static Pattern face_V_VN_Pattern = Pattern.compile("(f( \\d+//\\d+){3,4} *\\n)|(f( \\d+//\\d+){3,4} *$)");
    private static Pattern face_V_Pattern = Pattern.compile("(f( \\d+){3,4} *\\n)|(f( \\d+){3,4} *$)");
    private static Pattern objGroupObjectPattern = Pattern.compile("([go]( [\\w\\d]+) *\\n)|([go]( [\\w\\d]+) *$)");

    private static Matcher objVertexMatcher, objVertexNormalMatcher, objTextureCoordinateMatcher;
    private static Matcher face_V_VT_VN_Matcher, face_V_VT_Matcher, face_V_VN_Matcher, face_V_Matcher;
    private static Matcher objGroupObjectMatcher;

    public ArrayList<ObjVertex> vertices = new ArrayList<ObjVertex>();
    public ArrayList<ObjVertex> objVertexNormals = new ArrayList<ObjVertex>();
    public ArrayList<ObjTextureCoordinate> objTextureCoordinates = new ArrayList<ObjTextureCoordinate>();
    public ArrayList<ObjGroupObject> objGroupObjects = new ArrayList<ObjGroupObject>();
    private ObjGroupObject currentObjGroupObject;
    private String fileName;

    public WavefrontObject(ResourceLocation resource) throws ModelFormatException {
        this.fileName = resource.toString();

        try {
            IResource res = Minecraft.getInstance().getResourceManager().getResource(resource);
            loadObjModel(res.getInputStream());
        } catch (IOException e) {
            throw new ModelFormatException("IO Exception reading model format", e);
        }
    }

    public WavefrontObject(String filename, InputStream inputStream) throws ModelFormatException {
        this.fileName = filename;
        loadObjModel(inputStream);
    }

    private void loadObjModel(InputStream inputStream) throws ModelFormatException {
        BufferedReader reader = null;

        String currentLine = null;
        int lineCount = 0;

        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((currentLine = reader.readLine()) != null) {
                lineCount++;
                currentLine = currentLine.replaceAll("\\s+", " ").trim();

                if (currentLine.startsWith("#") || currentLine.length() == 0) {
                    continue;
                } else if (currentLine.startsWith("v ")) {
                    ObjVertex objVertex = parseObjVertex(currentLine, lineCount);
                    if (objVertex != null) {
                        vertices.add(objVertex);
                    }
                } else if (currentLine.startsWith("vn ")) {
                    ObjVertex objVertex = parseObjVertexNormal(currentLine, lineCount);
                    if (objVertex != null) {
                        objVertexNormals.add(objVertex);
                    }
                } else if (currentLine.startsWith("vt ")) {
                    ObjTextureCoordinate objTextureCoordinate = parseObjTextureCoordinate(currentLine, lineCount);
                    if (objTextureCoordinate != null) {
                        objTextureCoordinates.add(objTextureCoordinate);
                    }
                } else if (currentLine.startsWith("f ")) {

                    if (currentObjGroupObject == null) {
                        currentObjGroupObject = new ObjGroupObject("Default");
                    }

                    ObjFace face = parseFace(currentLine, lineCount);

                    if (face != null) {
                        currentObjGroupObject.faces.add(face);
                    }
                } else if (currentLine.startsWith("g ") | currentLine.startsWith("o ")) {
                    ObjGroupObject group = parseObjGroupObject(currentLine, lineCount);

                    if (group != null) {
                        if (currentObjGroupObject != null) {
                            objGroupObjects.add(currentObjGroupObject);
                        }
                    }

                    currentObjGroupObject = group;
                }
            }

            objGroupObjects.add(currentObjGroupObject);
        } catch (IOException e) {
            throw new ModelFormatException("IO Exception reading model format", e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // hush
            }

            try {
                inputStream.close();
            } catch (IOException e) {
                // hush
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderAll() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        tessellateAll(tessellator);
    }

    @OnlyIn(Dist.CLIENT)
    public void tessellateAll(Tessellator tessellator) {
        for (ObjGroupObject objGroupObject : objGroupObjects) {
            objGroupObject.render();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderOnly(String... groupNames) {
        for (ObjGroupObject objGroupObject : objGroupObjects) {
            for (String groupName : groupNames) {
                if (groupName.equalsIgnoreCase(objGroupObject.name)) {
                    objGroupObject.render();
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void tessellateOnly(Tessellator tessellator, String... groupNames) {
        for (ObjGroupObject objGroupObject : objGroupObjects) {
            for (String groupName : groupNames) {
                if (groupName.equalsIgnoreCase(objGroupObject.name)) {
                    objGroupObject.render(tessellator);
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderPart(String partName) {
        for (ObjGroupObject objGroupObject : objGroupObjects) {
            if (partName.equalsIgnoreCase(objGroupObject.name)) {
                objGroupObject.render();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void tessellatePart(Tessellator tessellator, String partName) {
        for (ObjGroupObject objGroupObject : objGroupObjects) {
            if (partName.equalsIgnoreCase(objGroupObject.name)) {
                objGroupObject.render(tessellator);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderAllExcept(String... excludedGroupNames) {
        for (ObjGroupObject objGroupObject : objGroupObjects) {
            boolean skipPart = false;
            for (String excludedGroupName : excludedGroupNames) {
                if (excludedGroupName.equalsIgnoreCase(objGroupObject.name)) {
                    skipPart = true;
                }
            }
            if (!skipPart) {
                objGroupObject.render();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void tessellateAllExcept(Tessellator tessellator, String... excludedGroupNames) {
        boolean exclude;
        for (ObjGroupObject objGroupObject : objGroupObjects) {
            exclude = false;
            for (String excludedGroupName : excludedGroupNames) {
                if (excludedGroupName.equalsIgnoreCase(objGroupObject.name)) {
                    exclude = true;
                }
            }
            if (!exclude) {
                objGroupObject.render(tessellator);
            }
        }
    }

    private ObjVertex parseObjVertex(String line, int lineCount) throws ModelFormatException {
        ObjVertex objVertex = null;

        if (isValidObjVertexLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");

            try {
                if (tokens.length == 2) {
                    return new ObjVertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]));
                } else if (tokens.length == 3) {
                    return new ObjVertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                }
            } catch (NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
        } else {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }

        return objVertex;
    }

    private ObjVertex parseObjVertexNormal(String line, int lineCount) throws ModelFormatException {
        ObjVertex objVertexNormal = null;

        if (isValidObjVertexNormalLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");

            try {
                if (tokens.length == 3)
                    return new ObjVertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
            } catch (NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
        } else {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }

        return objVertexNormal;
    }

    private ObjTextureCoordinate parseObjTextureCoordinate(String line, int lineCount) throws ModelFormatException {
        ObjTextureCoordinate objTextureCoordinate = null;

        if (isValidObjTextureCoordinateLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");

            try {
                if (tokens.length == 2)
                    return new ObjTextureCoordinate(Float.parseFloat(tokens[0]), 1 - Float.parseFloat(tokens[1]));
                else if (tokens.length == 3)
                    return new ObjTextureCoordinate(Float.parseFloat(tokens[0]), 1 - Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
            } catch (NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
        } else {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }

        return objTextureCoordinate;
    }

    private ObjFace parseFace(String line, int lineCount) throws ModelFormatException {
        ObjFace face = null;

        if (isValidFaceLine(line)) {
            face = new ObjFace();

            String trimmedLine = line.substring(line.indexOf(" ") + 1);
            String[] tokens = trimmedLine.split(" ");
            String[] subTokens = null;

            if (tokens.length == 3) {
                if (currentObjGroupObject.glDrawingMode == -1) {
                    currentObjGroupObject.glDrawingMode = GL11.GL_TRIANGLES;
                } else if (currentObjGroupObject.glDrawingMode != GL11.GL_TRIANGLES) {
                    throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Invalid number of points for face (expected 4, found " + tokens.length + ")");
                }
            } else if (tokens.length == 4) {
                if (currentObjGroupObject.glDrawingMode == -1) {
                    currentObjGroupObject.glDrawingMode = GL11.GL_QUADS;
                } else if (currentObjGroupObject.glDrawingMode != GL11.GL_QUADS) {
                    throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Invalid number of points for face (expected 3, found " + tokens.length + ")");
                }
            }

            // f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3 ...
            if (isValidFace_V_VT_VN_Line(line)) {
                face.vertices = new ObjVertex[tokens.length];
                face.objTextureCoordinates = new ObjTextureCoordinate[tokens.length];
                face.objVertexNormals = new ObjVertex[tokens.length];

                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("/");

                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.objTextureCoordinates[i] = objTextureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                    face.objVertexNormals[i] = objVertexNormals.get(Integer.parseInt(subTokens[2]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            }
            // f v1/vt1 v2/vt2 v3/vt3 ...
            else if (isValidFace_V_VT_Line(line)) {
                face.vertices = new ObjVertex[tokens.length];
                face.objTextureCoordinates = new ObjTextureCoordinate[tokens.length];

                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("/");

                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.objTextureCoordinates[i] = objTextureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            }
            // f v1//vn1 v2//vn2 v3//vn3 ...
            else if (isValidFace_V_VN_Line(line)) {
                face.vertices = new ObjVertex[tokens.length];
                face.objVertexNormals = new ObjVertex[tokens.length];

                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("//");

                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.objVertexNormals[i] = objVertexNormals.get(Integer.parseInt(subTokens[1]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            }
            // f v1 v2 v3 ...
            else if (isValidFace_V_Line(line)) {
                face.vertices = new ObjVertex[tokens.length];

                for (int i = 0; i < tokens.length; ++i) {
                    face.vertices[i] = vertices.get(Integer.parseInt(tokens[i]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            } else {
                throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
            }
        } else {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }

        return face;
    }

    private ObjGroupObject parseObjGroupObject(String line, int lineCount) throws ModelFormatException {
        ObjGroupObject group = null;

        if (isValidObjGroupObjectLine(line)) {
            String trimmedLine = line.substring(line.indexOf(" ") + 1);

            if (trimmedLine.length() > 0) {
                group = new ObjGroupObject(trimmedLine);
            }
        } else {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }

        return group;
    }

    /***
     * Verifies that the given line from the model file is a valid objVertex
     * @param line the line being validated
     * @return true if the line is a valid objVertex, false otherwise
     */
    private static boolean isValidObjVertexLine(String line) {
        if (objVertexMatcher != null) {
            objVertexMatcher.reset();
        }

        objVertexMatcher = objVertexPattern.matcher(line);
        return objVertexMatcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid objVertex normal
     * @param line the line being validated
     * @return true if the line is a valid objVertex normal, false otherwise
     */
    private static boolean isValidObjVertexNormalLine(String line) {
        if (objVertexNormalMatcher != null) {
            objVertexNormalMatcher.reset();
        }

        objVertexNormalMatcher = objVertexNormalPattern.matcher(line);
        return objVertexNormalMatcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid texture coordinate
     * @param line the line being validated
     * @return true if the line is a valid texture coordinate, false otherwise
     */
    private static boolean isValidObjTextureCoordinateLine(String line) {
        if (objTextureCoordinateMatcher != null) {
            objTextureCoordinateMatcher.reset();
        }

        objTextureCoordinateMatcher = objTextureCoordinatePattern.matcher(line);
        return objTextureCoordinateMatcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face that is described by vertices, texture coordinates, and objVertex normals
     * @param line the line being validated
     * @return true if the line is a valid face that matches the format "f v1/vt1/vn1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
     */
    private static boolean isValidFace_V_VT_VN_Line(String line) {
        if (face_V_VT_VN_Matcher != null) {
            face_V_VT_VN_Matcher.reset();
        }

        face_V_VT_VN_Matcher = face_V_VT_VN_Pattern.matcher(line);
        return face_V_VT_VN_Matcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face that is described by vertices and texture coordinates
     * @param line the line being validated
     * @return true if the line is a valid face that matches the format "f v1/vt1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
     */
    private static boolean isValidFace_V_VT_Line(String line) {
        if (face_V_VT_Matcher != null) {
            face_V_VT_Matcher.reset();
        }

        face_V_VT_Matcher = face_V_VT_Pattern.matcher(line);
        return face_V_VT_Matcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face that is described by vertices and objVertex normals
     * @param line the line being validated
     * @return true if the line is a valid face that matches the format "f v1//vn1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
     */
    private static boolean isValidFace_V_VN_Line(String line) {
        if (face_V_VN_Matcher != null) {
            face_V_VN_Matcher.reset();
        }

        face_V_VN_Matcher = face_V_VN_Pattern.matcher(line);
        return face_V_VN_Matcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face that is described by only vertices
     * @param line the line being validated
     * @return true if the line is a valid face that matches the format "f v1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
     */
    private static boolean isValidFace_V_Line(String line) {
        if (face_V_Matcher != null) {
            face_V_Matcher.reset();
        }

        face_V_Matcher = face_V_Pattern.matcher(line);
        return face_V_Matcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face of any of the possible face formats
     * @param line the line being validated
     * @return true if the line is a valid face that matches any of the valid face formats, false otherwise
     */
    private static boolean isValidFaceLine(String line) {
        return isValidFace_V_VT_VN_Line(line) || isValidFace_V_VT_Line(line) || isValidFace_V_VN_Line(line) || isValidFace_V_Line(line);
    }

    /***
     * Verifies that the given line from the model file is a valid group (or object)
     * @param line the line being validated
     * @return true if the line is a valid group (or object), false otherwise
     */
    private static boolean isValidObjGroupObjectLine(String line) {
        if (objGroupObjectMatcher != null) {
            objGroupObjectMatcher.reset();
        }

        objGroupObjectMatcher = objGroupObjectPattern.matcher(line);
        return objGroupObjectMatcher.matches();
    }

    @Override
    public String getType() {
        return "obj";
    }
}
