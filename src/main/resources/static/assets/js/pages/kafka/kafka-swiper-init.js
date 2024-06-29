
// Swiper Slider
var swiper = new Swiper(".cryptoSlider", {
    slidesPerView: 1,
    loop: false,
    spaceBetween: 24,
    navigation: {
      nextEl: ".swiper-button-next",
      prevEl: ".swiper-button-prev",
    },
    autoplay: {
      delay: 2500,
      disableOnInteraction: false,
    },
    breakpoints: {
      640: {
        slidesPerView: 2,
      },
      768: {
        slidesPerView: 2.5,
      },
      1024: {
        slidesPerView: 3,
      },
      1200: {
        slidesPerView: 5,
      },
    },
  });