x = [1:50]
nhit = [61.56, 76.73, 80.45, 81.61, 79.56, 77.21, 73.44, 68.74, 63.51, 57.23, 53.17, 47.22, 42.97, 38.67, 34.91, 31.84, 29.21, 25.18, 23.76, 19.91, 17.87, 16.73, 15.6, 13.63, 12.1, 12.05, 11.81, 9.79, 9.38, 9.05, 8.77, 7.26, 7.65, 6.6, 6.21, 5.25, 5.36, 5.45, 4.46, 4.56, 4.65, 3.45, 3.67, 3.96, 3.57, 2.79, 2.79, 2.91, 2.88, 2.47]
fhit = [97.16, 97.17, 96.54, 93.99, 89.93, 85.14, 79.43, 73.39, 66.42, 59.2, 54.18, 48.34, 44.18, 39.17, 35.77, 32.83, 28.47, 25.32, 23.08, 19.89, 18.61, 16.96, 15.58, 13.94, 11.97, 12.78, 10.87, 9.75, 10.26, 8.6, 9.12, 7.61, 7.41, 6.27, 6.21, 4.79, 5.48, 5.23, 4.35, 4.39, 4.73, 3.66, 3.65, 3.91, 3.85, 2.91, 2.75, 3.25, 2.97, 2.36]
pmax = plot(x,fhit,x,nhit);
ylabel("Success rate: first hit (%)");
title("Success rate versus sampling rate: Noisy OneMax");
xlabel("Resampling rate");

