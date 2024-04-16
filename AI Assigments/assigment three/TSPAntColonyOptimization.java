package global.network.sob.ksa.out;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TSPAntColonyOptimization {

    static class Location {
        int id;
        int x;
        int y;

        public Location(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    static class Ant {
        List<Location> tour;
        double tourLength;

        public Ant() {
            this.tour = new ArrayList<>();
            this.tourLength = 0;
        }

        public void addLocation(Location loc) {
            tour.add(loc);
        }

        public void calculateTourLength() {
            tourLength = 0;
            for (int i = 0; i < tour.size() - 1; i++) {
                Location current = tour.get(i);
                Location next = tour.get(i + 1);
                tourLength += Math.sqrt(Math.pow(next.x - current.x, 2) + Math.pow(next.y - current.y, 2));
            }
            tourLength += Math.sqrt(Math.pow(tour.get(0).x - tour.get(tour.size() - 1).x, 2)
                    + Math.pow(tour.get(0).y - tour.get(tour.size() - 1).y, 2)); // Return to start
        }
    }
    public static List<Location> readLocationsFromFile(String filename) {
        List<Location> locations = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0].trim());
                    int x = Integer.parseInt(parts[1].trim());
                    int y = Integer.parseInt(parts[2].trim());
                    locations.add(new Location(id, x, y));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return locations;
    }

    static List<Location> locations = new ArrayList<>();
    static int maxDistance = 9000;
    static int maxIterations = 500;
    static int numAnts = 20;
    static double evaporation = 0.5;
    static double alpha = 1.0;
    static double beta = 2.0;
    static Random random = new Random();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the filename containing locations: ");
        String filename = scanner.nextLine();

        List<Location> locations = readLocationsFromFile(filename);

        // Create initial pheromone matrix
        double[][] pheromones = new double[locations.size()][locations.size()];
        double[][] distances = calculateDistances(locations);

        // Initialize ants
        List<Ant> ants = new ArrayList<>();
        for (int i = 0; i < numAnts; i++) {
            ants.add(new Ant());
        }

        double shortestTour = Double.MAX_VALUE;
        List<Location> bestTour = null;

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            // Move ants
            for (Ant ant : ants) {
                constructSolution(ant, pheromones, distances);
                ant.calculateTourLength();

                if (ant.tourLength < shortestTour) {
                    shortestTour = ant.tourLength;
                    bestTour = new ArrayList<>(ant.tour);
                }
            }

            // Update pheromones
            updatePheromones(pheromones, ants);

            // Evaporate pheromones
            evaporatePheromones(pheromones);
        }

        // Print best tour
        if (bestTour != null) {
            System.out.println("Best Tour:");
            for (Location loc : bestTour) {
                System.out.print(loc.id + " -> ");
            }
            System.out.println(bestTour.get(0).id); // Return to start
            System.out.println("Total Distance: " + shortestTour);
        }
    }

    public static double[][] calculateDistances(List<Location> locations) {
        int numLocations = locations.size();
        double[][] distances = new double[numLocations][numLocations];
        for (int i = 0; i < numLocations; i++) {
            for (int j = 0; j < numLocations; j++) {
                Location loc1 = locations.get(i);
                Location loc2 = locations.get(j);
                distances[i][j] = Math.sqrt(Math.pow(loc2.x - loc1.x, 2) + Math.pow(loc2.y - loc1.y, 2));
            }
        }
        return distances;
    }

    public static int selectNextLocation(int currentIndex, List<Location> unvisited, double[][] pheromones,
                                         double[][] distances) {
        double total = 0;
        int numLocations = unvisited.size(); // Use size of unvisited locations
        double[] probabilities = new double[numLocations];

        for (int i = 0; i < numLocations; i++) {
            Location loc = unvisited.get(i);
            double pheromone = pheromones[currentIndex][locations.indexOf(loc)]; // <-- Potential issue
            double distance = distances[currentIndex][locations.indexOf(loc)];
            probabilities[i] = Math.pow(pheromone, alpha) * Math.pow(1.0 / distance, beta);
            total += probabilities[i];
        }

        double rand = random.nextDouble();
        double sum = 0;
        for (int i = 0; i < numLocations; i++) {
            sum += probabilities[i] / total;
            if (rand <= sum) {
                return i;
            }
        }
        return -1;
    }

    public static void constructSolution(Ant ant, double[][] pheromones, double[][] distances) {
        List<Location> unvisited = new ArrayList<>(locations);
        Location current = unvisited.remove(0);
        ant.addLocation(current);

        while (!unvisited.isEmpty()) {
            int currentIndex = locations.indexOf(current);
            int nextIndex = selectNextLocation(currentIndex, unvisited, pheromones, distances);
            if (nextIndex == -1) {
                break; // No valid next location found
            }
            current = unvisited.get(nextIndex);
            unvisited.remove(nextIndex);
            ant.addLocation(current);
        }
    }



    public static void updatePheromones(double[][] pheromones, List<Ant> ants) {
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones[0].length; j++) {
                pheromones[i][j] *= (1 - evaporation);
            }
        }

        for (Ant ant : ants) {
            double deltaPheromone = 1.0 / ant.tourLength;
            for (int i = 0; i < ant.tour.size() - 1; i++) {
                Location loc1 = ant.tour.get(i);
                Location loc2 = ant.tour.get(i + 1);
                int index1 = locations.indexOf(loc1);
                int index2 = locations.indexOf(loc2);
                pheromones[index1][index2] += deltaPheromone;
                pheromones[index2][index1] += deltaPheromone;
            }
        }
    }

    public static void evaporatePheromones(double[][] pheromones) {
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones[0].length; j++) {
                pheromones[i][j] *= (1 - evaporation);
            }
        }
    }
}

