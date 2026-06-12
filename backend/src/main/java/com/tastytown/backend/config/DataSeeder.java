package com.tastytown.backend.config;

import com.tastytown.backend.model.Category;
import com.tastytown.backend.model.Food;
import com.tastytown.backend.repository.CategoryRepository;
import com.tastytown.backend.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private static final List<CuratedFoodSeed> CURATED_IMAGE_FOODS = List.of(
        new CuratedFoodSeed("fb8f6b55-21d7-49b1-8a90-ec4a61e8694c.jpg", "Chicken Biryani", "Aromatic biryani rice layered with tender chicken and warm spices.", 299.0, "Indian"),
        new CuratedFoodSeed("fb8f6b55-21d7-49b1-8a90-ec4a61e8694c.jpg", "Hyderabadi Dum Biryani", "Slow-cooked dum biryani with fragrant basmati rice, herbs, and deep royal spices.", 329.0, "Indian"),
        new CuratedFoodSeed("fb8f6b55-21d7-49b1-8a90-ec4a61e8694c.jpg", "Mutton Biryani", "Tender mutton pieces layered with saffron rice and traditional biryani masala.", 359.0, "Indian"),
        new CuratedFoodSeed("fb8f6b55-21d7-49b1-8a90-ec4a61e8694c.jpg", "Egg Biryani", "Flavorful basmati rice tossed with masala-coated eggs and caramelized onions.", 249.0, "Indian"),
        new CuratedFoodSeed("fb8f6b55-21d7-49b1-8a90-ec4a61e8694c.jpg", "Paneer Biryani", "Aromatic rice layered with spiced paneer cubes, mint, and fried onions.", 279.0, "Indian"),
        new CuratedFoodSeed("48be7361-41c7-4c8d-8bb5-fbe4ce73085c1764767569245.jpg", "Veg Biryani", "Fragrant vegetable biryani with basmati rice, mixed vegetables, and gentle spices.", 229.0, "Indian"),
        new CuratedFoodSeed("df8b11d1-6cf7-4573-b9fd-a6ee471b39531764734260966.jpg", "Chicken Dum Biryani", "Classic dum-style chicken biryani served with rich spices and tender chicken pieces.", 329.0, "Indian"),
        new CuratedFoodSeed("e39bbc8a-28da-4e0e-8e07-99012cf45c75.jpg", "Farmhouse Pizza", "Loaded pizza with olives, peppers, onions, herbs, and melted cheese.", 279.0, "Italian"),
        new CuratedFoodSeed("92b2284c-cfb0-47f4-85f7-bc10744f0dab1764768012242.jpg", "Garlic Noodles", "Saucy garlic noodles tossed with spring onions and a light chilli kick.", 189.0, "Fast Food"),
        new CuratedFoodSeed("5948c318-0a3b-4d23-b593-e71bdc7c2cce.jpg", "Paneer Butter Masala", "Soft paneer cubes simmered in a rich buttery tomato gravy.", 249.0, "Indian"),
        new CuratedFoodSeed("ffc62c17-57ec-4f14-acfc-1cdb496869f7.jpg", "Shahi Paneer", "Creamy paneer curry finished with fresh herbs and a royal spice blend.", 259.0, "Indian"),
        new CuratedFoodSeed("64e8d3ff-b4bb-41f8-b6aa-a7159dc5cab6.jpg", "Veg Manchurian", "Crispy vegetable balls tossed in a glossy spicy Indo-Chinese sauce.", 199.0, "Fast Food"),
        new CuratedFoodSeed("e0649018-9480-424c-9eec-9239b2990ae8.jpg", "Chocolate Shake", "Chilled chocolate milkshake topped with whipped cream and chocolate drizzle.", 159.0, "Beverages"),
        new CuratedFoodSeed("dcb96851-6102-4c53-8454-0987da8b4d0e.jpg", "Blueberry Shake", "Creamy blueberry shake layered with berries and crunchy toppings.", 179.0, "Beverages"),
        new CuratedFoodSeed("a50b3a88-3d17-43b7-9e5f-547a9f484975.jpg", "Berry Freakshake", "Loaded mixed-berry shake finished with whipped cream and fruit topping.", 199.0, "Beverages"),
        new CuratedFoodSeed("1d74ff71-b280-4507-9ba1-ca42c53bcfff.jpg", "Blackberry Smoothie", "Refreshing blackberry smoothie blended smooth and served extra cold.", 169.0, "Beverages"),
        new CuratedFoodSeed("5c3681d8-c2d0-4770-8821-52aeae7f4d22.jpg", "Ice Cream Sundae", "Tall sundae stacked with assorted scoops, sauce, nuts, and cherries.", 189.0, "Desserts"),
        new CuratedFoodSeed("79dc8192-f3a6-46b2-ad3a-9d5d1902f367.jpg", "Chocolate Vanilla Scoop Duo", "Classic vanilla and chocolate scoops with chocolate chunks and sauce.", 129.0, "Desserts"),
        new CuratedFoodSeed("e543fd36-819f-4344-b318-bac336553450.jpg", "Chocolate Ice Cream Bowl", "Rich chocolate ice cream scoops served with extra chocolate chips.", 119.0, "Desserts"),
        new CuratedFoodSeed("1430a7ba-17f4-4ccd-ba16-de787e2e2636.jpg", "Jalebi", "Fresh syrupy jalebi spirals with a bright festive crunch.", 99.0, "Desserts"),
        new CuratedFoodSeed("6a2aafd6-9cf0-424a-8d64-6e46cadab7a2.jpg", "Kesar Rasmalai", "Soft saffron rasmalai soaked in sweet creamy milk.", 139.0, "Desserts"),
        new CuratedFoodSeed("e7374c07-39d3-4d4e-9db0-5f1949039517.jpg", "Gulab Jamun Classic", "Soft gulab jamuns soaked in warm sugar syrup and topped with pistachio.", 129.0, "Desserts"),
        new CuratedFoodSeed("d446ebfa-84cb-45b6-900c-43d4778936ae.jpg", "Hot Gulab Jamun", "Freshly served hot gulab jamuns with a rich syrupy finish.", 139.0, "Desserts"),
        new CuratedFoodSeed("63ea86cd-6cbe-43af-9ea8-1eeaf25b9d7c.jpg", "Tandoori Sweet Potato", "Roasted sweet potato wedges served with creamy and tangy dips.", 189.0, "Street Food"),
        new CuratedFoodSeed("fd35f6b5-d845-4f16-9e1e-5e64142c68cf.jpg", "Pav Bhaji", "Mumbai-style pav bhaji with buttered buns, onions, and lime.", 199.0, "Street Food"),
        new CuratedFoodSeed("11f6b314-3c00-4246-8e68-c5f24ce47ff2.jpg", "Masala Chai", "Hot masala chai brewed strong and served fresh.", 49.0, "Beverages"),
        new CuratedFoodSeed("fa05d947-c073-476d-826c-3c5520a8a579.jpg", "Crispy Samosa", "Golden samosas served with chutney and evening chai vibes.", 79.0, "Street Food"),
        new CuratedFoodSeed("72e4104a-9953-44a1-b553-b1457dfd4111.jpg", "Indian Combo Thali", "Comforting Indian meal with curries, rice, and soft breads.", 349.0, "Indian"),
        new CuratedFoodSeed("bf45076e-6476-44c0-b779-e55be795385a.jpg", "Paneer Kathi Roll", "Spiced paneer wrap packed with onions, cucumber, and creamy dip.", 189.0, "Street Food"),
        new CuratedFoodSeed("421b3048-f917-4280-b542-0b7a07e8b771.jpg", "Rasmalai Royal", "Delicate rasmalai with saffron, nuts, and rich sweet milk.", 149.0, "Desserts"),
        new CuratedFoodSeed("391a1d02-8f37-442b-aafe-a067047df546.jpg", "Tandoori Chicken", "Smoky tandoori chicken leg pieces with lime and onion on the side.", 329.0, "Indian"),
        new CuratedFoodSeed("39ce6e0e-7424-4c11-9588-bb7133a13982.jpg", "Grilled Fish Tikka", "Char-grilled fish fillet served with green chutney and pickled onions.", 349.0, "Seafood"),
        new CuratedFoodSeed("49923124-ed02-4a4b-8da5-221e920edb35.jpg", "Egg Masala Curry", "Boiled eggs cooked in a thick spiced onion-tomato gravy.", 229.0, "Indian"),
        new CuratedFoodSeed("bcefb2b5-1df1-4160-8ebe-2b07e15ca8dc.jpg", "Paneer Curry Platter", "Paneer curry served with naan and rice for a full meal.", 319.0, "Indian"),
        new CuratedFoodSeed("7bccb7b1-fd9b-46ff-b587-84c66462beb2.jpg", "Rasgulla Classic", "Soft white rasgullas in light sugar syrup with saffron notes.", 119.0, "Desserts"),
        new CuratedFoodSeed("1cfba12a-b2bf-44c7-831e-7de6dc378bc5.jpg", "Rasgulla Bowl", "Fresh rasgullas served chilled in sweet syrup.", 129.0, "Desserts"),
        new CuratedFoodSeed("d417a4bd-bced-4074-95f3-12dcce96285a.jpg", "Kesar Rasmalai Deluxe", "Premium rasmalai topped with nuts, saffron, and silver leaf.", 159.0, "Desserts"),
        new CuratedFoodSeed("53401a5e-dfbf-4661-8272-3f4f0729792e.jpg", "Rasgulla Special", "Spongy rasgullas in fragrant syrup with pistachio garnish.", 139.0, "Desserts"),
        new CuratedFoodSeed("573b72e8-d7b5-4ce4-9604-5028c7a16253.jpg", "Besan Laddoo", "Traditional laddoos with a festive nut topping.", 109.0, "Desserts"),
        new CuratedFoodSeed("e9c10bb4-76db-4430-a084-0dcd10197c19.jpg", "Strawberry Milkshake", "Strawberry milkshake with whipped cream and fresh strawberry slices.", 169.0, "Beverages")
    );

    private final CategoryRepository categoryRepository;
    private final FoodRepository foodRepository;

    @Value("${upload.image.path}")
    private String imagesFolderPath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Path uploadDirectory = ensureUploadDirectory();
        Map<String, Category> categories = initializeCategories();

        if (foodRepository.count() == 0) {
            seedDefaultFoods(uploadDirectory, categories);
        } else {
            log.info("[DataSeeder] Existing foods found. Skipping bundled seed data.");
        }

        importCuratedImageFoods(uploadDirectory, categories);
        importUnmappedImageFoods(uploadDirectory, categories);
    }

    private Path ensureUploadDirectory() throws IOException {
        Path uploadDirectory = Path.of(imagesFolderPath).toAbsolutePath().normalize();
        Files.createDirectories(uploadDirectory);
        log.info("[DataSeeder] Using image directory: {}", uploadDirectory);
        return uploadDirectory;
    }

    private Map<String, Category> initializeCategories() {
        List<String> categoryNames = List.of(
            "Fast Food",
            "Italian",
            "Indian",
            "Healthy",
            "Desserts",
            "Beverages",
            "Street Food",
            "Seafood",
            "Other"
        );

        Map<String, Category> categories = new LinkedHashMap<>();
        for (String categoryName : categoryNames) {
            categories.put(categoryName, getOrCreateCategory(categoryName));
        }

        log.info("[DataSeeder] Categories ready: {}", categories.keySet());
        return categories;
    }

    private Category getOrCreateCategory(String categoryName) {
        return categoryRepository.findByCategoryNameIgnoreCase(categoryName)
            .orElseGet(() -> {
                log.info("[DataSeeder] Creating category: {}", categoryName);
                return categoryRepository.save(Category.builder().categoryName(categoryName).build());
            });
    }

    private void seedDefaultFoods(Path uploadDirectory, Map<String, Category> categories) {
        log.info("[DataSeeder] Seeding bundled starter foods...");

        String burgerImage = copyBundledSeedImage(uploadDirectory, "seed-images/burger.png", "burger.png");
        String pizzaImage = copyBundledSeedImage(uploadDirectory, "seed-images/pizza.png", "pizza.png");
        String biryaniImage = copyBundledSeedImage(uploadDirectory, "seed-images/biryani.png", "biryani.png");
        String pastaImage = copyBundledSeedImage(uploadDirectory, "seed-images/pasta.png", "pasta.png");
        String chocolateCakeImage = copyBundledSeedImage(uploadDirectory, "seed-images/chocolate_cake.png", "chocolate_cake.png");
        String caesarSaladImage = copyBundledSeedImage(uploadDirectory, "seed-images/caesar_salad.png", "caesar_salad.png");
        String rasgullaImage = copyBundledSeedImage(uploadDirectory, "seed-images/rasgulla.png", "rasgulla.png");
        String kesarRasmalaiImage = copyBundledSeedImage(uploadDirectory, "seed-images/kesar_rasmalai.png", "kesar_rasmalai.png");
        String iceCreamSundaeImage = copyBundledSeedImage(uploadDirectory, "seed-images/ice_cream_sundae.png", "ice_cream_sundae.png");
        String blackberrySmoothieImage = copyBundledSeedImage(uploadDirectory, "seed-images/blackberry_smoothie.png", "blackberry_smoothie.png");

        List<Food> foods = List.of(
            Food.builder()
                .foodName("Classic Beef Burger")
                .foodDescription("Juicy beef patty with fresh lettuce, tomato, cheese and pickles.")
                .foodPrice(8.99)
                .foodImage(burgerImage)
                .category(categories.get("Fast Food"))
                .build(),
            Food.builder()
                .foodName("Crispy French Fries")
                .foodDescription("Golden crispy fries with a hint of sea salt, served hot.")
                .foodPrice(3.49)
                .foodImage(burgerImage)
                .category(categories.get("Fast Food"))
                .build(),
            Food.builder()
                .foodName("Margherita Pizza")
                .foodDescription("Classic pizza with mozzarella, fresh basil, and tomato sauce.")
                .foodPrice(11.99)
                .foodImage(pizzaImage)
                .category(categories.get("Italian"))
                .build(),
            Food.builder()
                .foodName("Spaghetti Carbonara")
                .foodDescription("Creamy pasta with crispy pancetta and freshly grated parmesan.")
                .foodPrice(12.49)
                .foodImage(pastaImage)
                .category(categories.get("Italian"))
                .build(),
            Food.builder()
                .foodName("Chicken Biryani")
                .foodDescription("Aromatic saffron rice with tender chicken and fragrant spices.")
                .foodPrice(13.99)
                .foodImage(biryaniImage)
                .category(categories.get("Indian"))
                .build(),
            Food.builder()
                .foodName("Butter Chicken")
                .foodDescription("Tender chicken in rich, creamy tomato-based curry sauce.")
                .foodPrice(14.49)
                .foodImage(biryaniImage)
                .category(categories.get("Indian"))
                .build(),
            Food.builder()
                .foodName("Caesar Salad")
                .foodDescription("Crisp romaine lettuce, croutons, parmesan, and Caesar dressing.")
                .foodPrice(9.99)
                .foodImage(caesarSaladImage)
                .category(categories.get("Healthy"))
                .build(),
            Food.builder()
                .foodName("Chocolate Lava Cake")
                .foodDescription("Warm molten chocolate cake served with vanilla ice cream.")
                .foodPrice(6.99)
                .foodImage(chocolateCakeImage)
                .category(categories.get("Desserts"))
                .build(),
            Food.builder()
                .foodName("Rasgulla")
                .foodDescription("Soft cottage cheese balls soaked in rose-flavored sugar syrup.")
                .foodPrice(4.99)
                .foodImage(rasgullaImage)
                .category(categories.get("Desserts"))
                .build(),
            Food.builder()
                .foodName("Kesar Rasmalai")
                .foodDescription("Saffron-infused dumplings soaked in sweet saffron milk.")
                .foodPrice(5.49)
                .foodImage(kesarRasmalaiImage)
                .category(categories.get("Desserts"))
                .build(),
            Food.builder()
                .foodName("Rasgulla Special")
                .foodDescription("Premium extra-soft rasgullas on a decorative plate.")
                .foodPrice(5.99)
                .foodImage(rasgullaImage)
                .category(categories.get("Indian"))
                .build(),
            Food.builder()
                .foodName("Ice Cream Sundae")
                .foodDescription("Grand tower of assorted ice cream scoops with chocolate sauce.")
                .foodPrice(7.99)
                .foodImage(iceCreamSundaeImage)
                .category(categories.get("Desserts"))
                .build(),
            Food.builder()
                .foodName("Blackberry Smoothie")
                .foodDescription("Fresh blackberry blend with a burst of antioxidants, served chilled.")
                .foodPrice(4.49)
                .foodImage(blackberrySmoothieImage)
                .category(categories.get("Beverages"))
                .build()
        );

        foodRepository.saveAll(foods);
        log.info("[DataSeeder] Saved {} bundled food items.", foods.size());
    }

    private void importCuratedImageFoods(Path uploadDirectory, Map<String, Category> categories) {
        List<Path> sourceDirectories = resolveImportSourceDirectories(uploadDirectory);
        if (sourceDirectories.isEmpty()) {
            log.warn("[DataSeeder] No image source directories were found for curated image import.");
            return;
        }

        int importedCount = 0;
        for (CuratedFoodSeed seed : CURATED_IMAGE_FOODS) {
            if (foodRepository.existsByFoodNameIgnoreCase(seed.foodName())) {
                continue;
            }

            Path sourceImage = findImageSource(seed.imageFileName(), sourceDirectories);
            if (sourceImage == null) {
                log.warn("[DataSeeder] Missing curated image: {}", seed.imageFileName());
                continue;
            }

            try {
                copyLocalImageIfNeeded(sourceImage, uploadDirectory.resolve(seed.imageFileName()));
                Category category = categories.computeIfAbsent(seed.categoryName(), this::getOrCreateCategory);

                Food food = Food.builder()
                    .foodName(seed.foodName())
                    .foodDescription(seed.foodDescription())
                    .foodPrice(seed.foodPrice())
                    .foodImage(seed.imageFileName())
                    .category(category)
                    .build();

                foodRepository.save(food);
                importedCount++;
            } catch (IOException exception) {
                log.error("[DataSeeder] Failed to import curated image food: {}", seed.imageFileName(), exception);
            }
        }

        log.info("[DataSeeder] Imported {} curated image foods.", importedCount);
    }

    private void importUnmappedImageFoods(Path uploadDirectory, Map<String, Category> categories) {
        List<Path> sourceDirectories = resolveImportSourceDirectories(uploadDirectory);
        if (sourceDirectories.isEmpty()) {
            log.warn("[DataSeeder] No image source directories were found for automatic image import.");
            return;
        }

        Category defaultCategory = categories.computeIfAbsent("Other", this::getOrCreateCategory);
        Set<String> processedImageNames = new LinkedHashSet<>();
        int importedCount = 0;

        for (Path sourceDirectory : sourceDirectories) {
            try (var imagePaths = Files.list(sourceDirectory)) {
                for (Path sourceImage : imagePaths
                    .filter(Files::isRegularFile)
                    .filter(this::isSupportedImageFile)
                    .toList()) {
                    String imageFileName = sourceImage.getFileName().toString();
                    String imageKey = imageFileName.toLowerCase(Locale.ROOT);

                    if (!processedImageNames.add(imageKey) || foodRepository.existsByFoodImage(imageFileName)) {
                        continue;
                    }

                    String foodName = buildFoodNameFromImageName(imageFileName);
                    if (foodRepository.existsByFoodNameIgnoreCase(foodName)) {
                        foodName = buildUniqueFoodName(foodName);
                    }

                    try {
                        copyLocalImageIfNeeded(sourceImage, uploadDirectory.resolve(imageFileName));

                        Food food = Food.builder()
                            .foodName(foodName)
                            .foodDescription("Freshly added item from the food photo gallery.")
                            .foodPrice(199.0)
                            .foodImage(imageFileName)
                            .category(defaultCategory)
                            .build();

                        foodRepository.save(food);
                        importedCount++;
                    } catch (IOException exception) {
                        log.error("[DataSeeder] Failed to import image food: {}", imageFileName, exception);
                    }
                }
            } catch (IOException exception) {
                log.error("[DataSeeder] Failed to scan image directory: {}", sourceDirectory, exception);
            }
        }

        log.info("[DataSeeder] Imported {} automatic image foods.", importedCount);
    }

    private List<Path> resolveImportSourceDirectories(Path uploadDirectory) {
        Set<Path> directories = new LinkedHashSet<>();
        directories.add(uploadDirectory);
        directories.add(Path.of(".").toAbsolutePath().normalize());
        directories.add(Path.of("images").toAbsolutePath().normalize());
        directories.add(Path.of("..").toAbsolutePath().normalize());
        directories.add(Path.of("..").resolve("images").toAbsolutePath().normalize());

        List<Path> existingDirectories = new ArrayList<>();
        for (Path directory : directories) {
            if (Files.isDirectory(directory)) {
                existingDirectories.add(directory);
            }
        }
        return existingDirectories;
    }

    private Path findImageSource(String imageFileName, List<Path> sourceDirectories) {
        for (Path directory : sourceDirectories) {
            Path candidate = directory.resolve(imageFileName);
            if (Files.exists(candidate) && Files.isRegularFile(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private boolean isSupportedImageFile(Path imagePath) {
        String fileName = imagePath.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.endsWith(".jpg")
            || fileName.endsWith(".jpeg")
            || fileName.endsWith(".png")
            || fileName.endsWith(".webp");
    }

    private String buildFoodNameFromImageName(String imageFileName) {
        String nameWithoutExtension = imageFileName.replaceFirst("\\.[^.]+$", "");
        String readableName = nameWithoutExtension
            .replaceAll("[^A-Za-z0-9]+", " ")
            .replaceAll("\\s+", " ")
            .trim();

        if (readableName.isBlank() || readableName.matches("[0-9a-fA-F\\s-]{20,}")) {
            return "Photo Food";
        }

        StringBuilder titleCaseName = new StringBuilder();
        for (String word : readableName.split(" ")) {
            if (word.isBlank()) {
                continue;
            }
            titleCaseName
                .append(Character.toUpperCase(word.charAt(0)))
                .append(word.length() > 1 ? word.substring(1).toLowerCase(Locale.ROOT) : "")
                .append(" ");
        }

        return titleCaseName.toString().trim();
    }

    private String buildUniqueFoodName(String baseFoodName) {
        int suffix = 2;
        String foodName = baseFoodName + " " + suffix;
        while (foodRepository.existsByFoodNameIgnoreCase(foodName)) {
            suffix++;
            foodName = baseFoodName + " " + suffix;
        }
        return foodName;
    }

    private void copyLocalImageIfNeeded(Path sourceImage, Path targetImage) throws IOException {
        if (sourceImage.toAbsolutePath().normalize().equals(targetImage.toAbsolutePath().normalize())) {
            return;
        }

        Files.createDirectories(targetImage.getParent());
        if (!Files.exists(targetImage)) {
            Files.copy(sourceImage, targetImage, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private String copyBundledSeedImage(Path uploadDirectory, String classpathPath, String targetFileName) {
        try {
            ClassPathResource resource = new ClassPathResource(classpathPath);
            if (!resource.exists()) {
                log.warn("[DataSeeder] Bundled seed image not found: {}", classpathPath);
                return null;
            }

            Path targetPath = uploadDirectory.resolve(targetFileName);
            if (Files.exists(targetPath)) {
                return targetFileName;
            }

            try (InputStream inputStream = resource.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return targetFileName;
        } catch (IOException exception) {
            log.error("[DataSeeder] Failed to copy bundled seed image: {}", classpathPath, exception);
            return null;
        }
    }

    private record CuratedFoodSeed(
        String imageFileName,
        String foodName,
        String foodDescription,
        double foodPrice,
        String categoryName
    ) {
    }
}
