png("foobar.png")
hist(PlantGrowth$weight)
dev.off()

# the above should work with html.image
html.add(
  hist,
  PlantGrowth$weight
)