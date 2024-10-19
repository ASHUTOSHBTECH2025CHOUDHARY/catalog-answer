import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ShamirSecretSharing {
    
    public static void main(String[] args) {
        try {
            // Read JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File("input.json"));

            // Extracting keys section
            JsonNode keysNode = rootNode.get("keys");
            int n = keysNode.get("n").asInt();
            int k = keysNode.get("k").asInt();
            System.out.println("Number of roots (n): " + n);
            System.out.println("Minimum roots required (k): " + k);

            // Decode the points
            Map<Integer, BigInteger> points = new HashMap<>();
            Iterator<String> fieldNames = rootNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                if (!fieldName.equals("keys")) {
                    JsonNode pointNode = rootNode.get(fieldName);
                    int x = Integer.parseInt(fieldName);
                    int base = pointNode.get("base").asInt();
                    String value = pointNode.get("value").asText();
                    BigInteger y = decodeValue(value, base);
                    points.put(x, y);
                }
            }

            // Perform Lagrange Interpolation to find constant term
            BigInteger constantTerm = lagrangeInterpolation(points, 0);
            System.out.println("The constant term (c) is: " + constantTerm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function to decode the value based on the base
    public static BigInteger decodeValue(String value, int base) {
        return new BigInteger(value, base);  // Converts the value from the given base to a BigInteger
    }

    // Lagrange interpolation method
    public static BigInteger lagrangeInterpolation(Map<Integer, BigInteger> points, int x) {
        BigInteger result = BigInteger.ZERO;

        for (Map.Entry<Integer, BigInteger> pointJ : points.entrySet()) {
            int xj = pointJ.getKey();
            BigInteger yj = pointJ.getValue();
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (Map.Entry<Integer, BigInteger> pointM : points.entrySet()) {
                int xm = pointM.getKey();
                if (xm != xj) {
                    numerator = numerator.multiply(BigInteger.valueOf(x - xm));
                    denominator = denominator.multiply(BigInteger.valueOf(xj - xm));
                }
            }

            BigInteger term = yj.multiply(numerator).divide(denominator);  // Lagrange term
            result = result.add(term);  // Summing up all terms
        }

        return result;
    }
}
