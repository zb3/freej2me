import sys
from PIL import Image

fname = sys.argv[1]
width = int(fname.split('x')[1].split('.')[0])
print('width', width)

with open(fname, 'rb') as f:
  data = f.read()

# Define the width and height of the image
height = len(data) // (width * 3)  # Assuming the data is tightly packed RGB pixels

# Create an Image object
image = Image.new('RGB', (width, height))

# Create a list of RGB tuples from the raw pixel data
pixel_data = [(data[i], data[i + 1], data[i + 2]) for i in range(0, len(data), 3)]

# Load the RGB tuple data into the image
image.putdata(pixel_data)


# Save the image as a BMP file
image.save('output.bmp', 'BMP')

print('Image converted and saved as "output.bmp"')
