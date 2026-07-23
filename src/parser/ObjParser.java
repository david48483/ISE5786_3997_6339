package parser;

import geometries.impl.Triangle;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import scene.Scene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple Wavefront OBJ file parser that reads vertices and faces and adds the
 * resulting triangle mesh into a {@link scene.Scene} with a uniform material and emission.
 */
public class ObjParser {

    /**
     * Utility class: prevent instantiation.
     */
    private ObjParser() {
    }

    /**
     * Reads an OBJ file and adds all of its triangular faces into the scene.
     * Indices are assumed to be 1-based as in the Wavefront format; only vertex positions are used.
     *
     * @param filePath path to the OBJ file
     * @param scene    the scene to which the triangles will be added
     * @param material material to assign to each triangle
     * @param emission emission color for the triangles
     */
    public static void parseAndAdd(String filePath, Scene scene, Material material, Color emission) {
        // רשימה לשמירת כל הנקודות במרחב
        List<Point> vertices = new ArrayList<>();

        // טריק קריטי: בקבצי OBJ, האינדקסים מתחילים מ-1 ולא מ-0!
        // לכן נוסיף נקודת "דמי" במקום ה-0 כדי שהאינדקסים יתאימו בדיוק לקובץ.
        vertices.add(new Point(0, 0, 0));

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                // קריאת קודקוד (Vertex)
                if (line.startsWith("v ")) {
                    String[] parts = line.split("\\s+");
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    vertices.add(new Point(x, y, z));
                }
                // קריאת משולש (Face)
                else if (line.startsWith("f ")) {
                    String[] parts = line.split("\\s+");

                    try {
                        // בבלנדר, הפנים מיוצאות לרוב בפורמט v/vt/vn
                        // אנחנו צריכים רק את המספר הראשון (הקודקוד), לכן נחתוך לפי "/"
                        int v1 = Integer.parseInt(parts[1].split("/")[0]);
                        int v2 = Integer.parseInt(parts[2].split("/")[0]);
                        int v3 = Integer.parseInt(parts[3].split("/")[0]);

                        Triangle t = new Triangle(vertices.get(v1), vertices.get(v2), vertices.get(v3));
                        t.setMaterial(material);
                        t.setEmission(emission);

                        scene.geometries.add(t);

                    } catch (IllegalArgumentException e) {
                        // תופס את השגיאה של "Zero vector is not allowed" ומתעלם מהמשולש הפגום
                    } catch (IndexOutOfBoundsException e) {
                        // תופס למקרה שיש שורת משולש שמפנה לנקודה שלא קיימת
                        System.err.println("Invalid vertex index in face definition: " + line);
                    }
                }
            }
        } catch (IOException e) {
            // הבלוק הזה היה חסר לך! תופס שגיאות של קריאת קובץ מהדיסק הקשיח
            System.err.println("Failed to read OBJ file: " + e.getMessage());
        }
    }
}