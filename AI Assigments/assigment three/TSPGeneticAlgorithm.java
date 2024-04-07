import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TSPGeneticAlgorithm {

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

    static class Route {
        List<Location> locations;
        double fitness;

        public Route(List<Location> locations) {
            this.locations = new ArrayList<>(locations);
            calculateFitness();
        }

        private void calculateFitness() {
            double totalDistance = 0;
            for (int i = 0; i < locations.size() - 1; i++) {
                Location current = locations.get(i);
                Location next = locations.get(i + 1);
                totalDistance += calculateDistance(current, next);
            }
            totalDistance += calculateDistance(locations.get(locations.size() - 1), locations.get(0)); // Return to start
            this.fitness = 1.0 / totalDistance;
        }

        private double calculateDistance(Location loc1, Location loc2) {
            return Math.sqrt(Math.pow(loc2.x - loc1.x, 2) + Math.pow(loc2.y - loc1.y, 2));
        }
    }

    static List<Location> locations = new ArrayList<>();
    static Random random = new Random();
    static int maxFitnessCalculations = 250000;
    static int maxDistance = 9000;

    public static void main(String[] args) {
        // Add locations
        locations.add(new Location(1, 565, 575));
        locations.add(new Location(2, 575, 25));
        locations.add(new Location(3, 345, 185));
        // Add more locations as needed

        // Create initial population
        int populationSize = 100;
        List<Route> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            List<Location> shuffledLocations = new ArrayList<>(locations);
            Collections.shuffle(shuffledLocations);
            population.add(new Route(shuffledLocations));
        }

        int generations = 0;
        while (generations < maxFitnessCalculations) {
            // Sort population by fitness
            Collections.sort(population, (r1, r2) -> Double.compare(r2.fitness, r1.fitness));

            // Check if best route is within max distance
            if (population.get(0).fitness > 1.0 / maxDistance) {
                break;
            }

            // Create new generation
            List<Route> newPopulation = new ArrayList<>();
            for (int i = 0; i < populationSize; i++) {
                Route parent1 = selectParent(population);
                Route parent2 = selectParent(population);
                Route child = crossover(parent1, parent2);
                mutate(child);
                newPopulation.add(child);
            }

            population = newPopulation;
            generations++;
        }

        // Print best route
        Route bestRoute = population.get(0);
        System.out.println("Best Route:");
        for (Location loc : bestRoute.locations) {
            System.out.print(loc.id + " -> ");
        }
        System.out.println(bestRoute.locations.get(0).id); // Return to start
        System.out.println("Total Distance: " + calculateTotalDistance(bestRoute));
        System.out.println("Fitness: " + bestRoute.fitness);
    }

    public static Route selectParent(List<Route> population) {
        int index = random.nextInt(population.size() / 2);
        return population.get(index);
    }

    public static Route crossover(Route parent1, Route parent2) {
        int size = parent1.locations.size();
        int start = random.nextInt(size);
        int end = random.nextInt(size - start) + start;
        List<Location> childLocations = new ArrayList<>(parent1.locations.subList(start, end));
        for (Location loc : parent2.locations) {
            if (!childLocations.contains(loc)) {
                childLocations.add(loc);
            }
        }
        return new Route(childLocations);
    }

    public static void mutate(Route route) {
        int size = route.locations.size();
        int index1 = random.nextInt(size);
        int index2 = random.nextInt(size);
        Collections.swap(route.locations, index1, index2);
        route.calculateFitness();
    }

    public static double calculateTotalDistance(Route route) {
        double totalDistance = 0;
        for (int i = 0; i < route.locations.size() - 1; i++) {
            Location current = route.locations.get(i);
            Location next = route.locations.get(i + 1);
            totalDistance += Math.sqrt(Math.pow(next.x - current.x, 2) + Math.pow(next.y - current.y, 2));
        }
        totalDistance += Math.sqrt(Math.pow(route.locations.get(0).x - route.locations.get(route.locations.size() - 1).x, 2)
                + Math.pow(route.locations.get(0).y - route.locations.get(route.locations.size() - 1).y, 2)); // Return to start
        return totalDistance;
    }
}
