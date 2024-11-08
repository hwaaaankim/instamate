!(function (e, t) {
  "object" == typeof exports && "undefined" != typeof module
    ? (module.exports = t())
    : "function" == typeof define && define.amd
    ? define(t)
    : ((e =
        "undefined" != typeof globalThis ? globalThis : e || self).SEMICOLON =
        t());
})(this, () => {
  "use strict";
  const s = {
      pageTransition: false,
      cursor: false,
      headerSticky: true,
      headerMobileSticky: false,
      menuBreakpoint: 992,
      pageMenuBreakpoint: 992,
      gmapAPI: "",
      scrollOffset: 60,
      scrollExternalLinks: true,
      jsFolder: "/front/js/",
      cssFolder: "/front/css/",
      jsLoadType: false,
    },
    d = {
      baseEl: document,
      elRoot: document.documentElement,
      elHead: document.head,
      elBody: document.body,
      hash: window.location.hash,
      topScrollOffset: 0,
      elWrapper: document.getElementById("wrapper"),
      elHeader: document.getElementById("header"),
      headerClasses: "",
      elHeaderWrap: document.getElementById("header-wrap"),
      headerWrapClasses: "",
      headerHeight: 0,
      headerOffset: 0,
      headerWrapHeight: 0,
      headerWrapOffset: 0,
      elPrimaryMenus: document.querySelectorAll(".primary-menu"),
      elPrimaryMenuTriggers: document.querySelectorAll(".primary-menu-trigger"),
      elPageMenu: document.getElementById("page-menu"),
      pageMenuOffset: 0,
      elSlider: document.getElementById("slider"),
      elFooter: document.getElementById("footer"),
      portfolioAjax: {},
      sliderParallax: {
        el: document.querySelector(".slider-parallax"),
        caption: document.querySelector(".slider-parallax .slider-caption"),
        inner: document.querySelector(".slider-inner"),
        offset: 0,
      },
      get menuBreakpoint() {
        return (
          this.elBody.getAttribute("data-menu-breakpoint") || s.menuBreakpoint
        );
      },
      get pageMenuBreakpoint() {
        return (
          this.elBody.getAttribute("data-pagemenu-breakpoint") ||
          s.pageMenuBreakpoint
        );
      },
      get customCursor() {
        var e = this.elBody.getAttribute("data-custom-cursor") || s.cursor;
        return "true" == e || !0 === e;
      },
      get pageTransition() {
        var e =
          this.elBody.classList.contains("page-transition") || s.pageTransition;
        return "true" == e || !0 === e;
      },
      scrollPos: { x: 0, y: 0 },
      $jq: "undefined" != typeof jQuery ? jQuery.noConflict() : "",
      resizers: {},
      recalls: {},
      debounced: !1,
      events: {},
      modules: {},
      fn: {},
      required: {
        jQuery: {
          plugin: "jquery",
          fn: () => "undefined" != typeof jQuery,
          file: s.jsFolder + "jquery.js",
          id: "canvas-jquery",
        },
      },
      fnInit: () => {
        n.init(), a.init(), i.init();
      },
    },
    l = {
      getOptions: s,
      getVars: d,
      run: (e) => {
        Object.values(e).map((e) => "function" == typeof e && e.call());
      },
      runBase: () => {
        l.run(e);
      },
      runModules: () => {
        l.run(t);
      },
      runContainerModules: (e) => {
        if (void 0 === e) return !1;
        (l.getVars.baseEl = e), l.runModules(), (l.getVars.baseEl = document);
      },
      breakpoints: () => {
        let t = l.viewport().width;
        const r = {
          xxl: { enter: 1400, exit: 99999 },
          xl: { enter: 1200, exit: 1399 },
          lg: { enter: 992, exit: 1199.98 },
          md: { enter: 768, exit: 991.98 },
          sm: { enter: 576, exit: 767.98 },
          xs: { enter: 0, exit: 575.98 },
        };
        let o = "";
        Object.keys(r).forEach((e) => {
          t > r[e].enter && t <= r[e].exit
            ? d.elBody.classList.add("device-" + e)
            : (d.elBody.classList.remove("device-" + e),
              "" != o && d.elBody.classList.remove("device-down-" + o)),
            t <= r[e].exit &&
              "" != o &&
              d.elBody.classList.add("device-down-" + o),
            (o = e),
            t > r[e].enter
              ? d.elBody.classList.add("device-up-" + e)
              : d.elBody.classList.remove("device-up-" + e);
        });
      },
      colorScheme: () => {
        d.elBody.classList.contains("adaptive-color-scheme") &&
          (window.matchMedia("(prefers-color-scheme: dark)").matches
            ? d.elBody.classList.add("dark")
            : d.elBody.classList.remove("dark"));
      },
      throttle: (e, t) => {
        let r;
        e(), (r = !0), setTimeout(function () {}, t);
      },
      debounce: (e, t) => {
        clearTimeout(d.debounced), (d.debounced = setTimeout(e, t));
      },
      addEvent: (e, t, r = {}) => {
        void 0 !== e &&
          void 0 !== t &&
          ((r = new CustomEvent(t, { detail: r })),
          e.dispatchEvent(r),
          (d.events[t] = !0));
      },
      scrollEnd: (e, t = 199) => {
        e &&
          "function" == typeof e &&
          window.addEventListener(
            "scroll",
            () => {
              l.debounce(e, t);
            },
            { passive: !0 }
          );
      },
      viewport: () => {
        return {
          width: window.innerWidth || d.elRoot.clientWidth,
          height: window.innerHeight || d.elRoot.clientHeight,
        };
      },
      getSelector: (e, t = !0, r = !0) => (
        t
          ? ((e =
              l.getVars.baseEl !== document
                ? jQuery(l.getVars.baseEl).find(e)
                : jQuery(e)),
            r &&
              (e =
                "string" == typeof r
                  ? e.filter(":not(" + r + ")")
                  : e.filter(":not(.customjs)")))
          : (e = r
              ? "string" == typeof r
                ? l.getVars.baseEl.querySelectorAll(e + ":not(" + r + ")")
                : l.getVars.baseEl.querySelectorAll(e + ":not(.customjs)")
              : l.getVars.baseEl.querySelectorAll(e)),
        e
      ),
      onResize: (e, t = 333) => {
        e &&
          "function" == typeof e &&
          (window.onresize = () => {
            l.debounce(e, t);
          });
      },
      imagesLoaded: (e) => {
        let t = e.getElementsByTagName("img") || document.images,
          r = t.length,
          o = 0;
        r < 1 && l.addEvent(e, "CanvasImagesLoaded");
        async function i() {
          ++o === r && l.addEvent(e, "CanvasImagesLoaded");
        }
        [].forEach.call(t, function (e) {
          e.complete ? i() : e.addEventListener("load", i, !1);
        });
      },
      contains: (e, t) => {
        e = e.split(" ");
        let r = !1;
        return (
          e.forEach((e) => {
            d.elBody.classList.contains(e) && (r = !0);
          }),
          r
        );
      },
      has: (e, t) => [...e].filter((e) => e.querySelector(t)),
      filtered: (e, t) => [...e].filter((e) => e.matches(t)),
      siblings: (t, e = !1) =>
        e
          ? [...e].filter((e) => e !== t)
          : [...t.parentNode.children].filter((e) => e !== t),
      getNext: (e, t) => {
        e = e.nextElementSibling;
        return !t || (e && e.matches(t)) ? e : null;
      },
      offset: (e) => {
        var e = e.getBoundingClientRect(),
          t = window.pageXOffset || document.documentElement.scrollLeft,
          r = window.pageYOffset || document.documentElement.scrollTop;
        return { top: e.top + r, left: e.left + t };
      },
      slideUp: (e, t = 500, r = !1) => {
        (e.style.transitionProperty = "height, margin, padding"),
          (e.style.transitionDuration = t + "ms"),
          (e.style.boxSizing = "border-box"),
          (e.style.height = e.offsetHeight + "px"),
          e.offsetHeight,
          (e.style.overflow = "hidden"),
          (e.style.height = 0),
          (e.style.paddingTop = 0),
          (e.style.paddingBottom = 0),
          (e.style.marginTop = 0),
          (e.style.marginBottom = 0),
          window.setTimeout(() => {
            (e.style.display = "none"),
              e.style.removeProperty("height"),
              e.style.removeProperty("padding-top"),
              e.style.removeProperty("padding-bottom"),
              e.style.removeProperty("margin-top"),
              e.style.removeProperty("margin-bottom"),
              e.style.removeProperty("overflow"),
              e.style.removeProperty("transition-duration"),
              e.style.removeProperty("transition-property"),
              "function" == typeof r && r();
          }, t);
      },
      slideDown: (e, t = 500, r = !1) => {
        e.style.removeProperty("display");
        let o = window.getComputedStyle(e).display;
        "none" === o && (o = "block"), (e.style.display = o);
        var i = e.offsetHeight;
        (e.style.overflow = "hidden"),
          (e.style.height = 0),
          (e.style.paddingTop = 0),
          (e.style.paddingBottom = 0),
          (e.style.marginTop = 0),
          (e.style.marginBottom = 0),
          e.offsetHeight,
          (e.style.boxSizing = "border-box"),
          (e.style.transitionProperty = "height, margin, padding"),
          (e.style.transitionDuration = t + "ms"),
          (e.style.height = i + "px"),
          e.style.removeProperty("padding-top"),
          e.style.removeProperty("padding-bottom"),
          e.style.removeProperty("margin-top"),
          e.style.removeProperty("margin-bottom"),
          window.setTimeout(() => {
            e.style.removeProperty("height"),
              e.style.removeProperty("overflow"),
              e.style.removeProperty("transition-duration"),
              e.style.removeProperty("transition-property"),
              "function" == typeof r && r();
          }, t);
      },
      slideToggle: (e, t = 500, r = !1) =>
        "none" === window.getComputedStyle(e).display
          ? l.slideDown(e, t, r)
          : l.slideUp(e, t, r),
      classesFn: (t, e, r) => {
        e.split(" ").forEach((e) => {
          "add" == t
            ? r.classList.add(e)
            : "toggle" == t
            ? r.classList.toggle(e)
            : r.classList.remove(e);
        });
      },
      loadCSS: (e) => {
        var t,
          r = e.file,
          o = e.id || !1,
          e = e.cssFolder || !1;
        return (
          !!r &&
          !(
            (o && document.getElementById(o)) ||
            (((t = document.createElement("link")).id = o),
            (t.href = e ? s.cssFolder + r : r),
            (t.rel = "stylesheet"),
            (t.type = "text/css"),
            d.elHead.appendChild(t),
            0)
          )
        );
      },
      loadJS: (e) => {
        var t = e.file,
          r = e.id || !1,
          o = e.type || !1,
          i = e.callback,
          l = e.async || !0,
          n = e.defer || !0,
          e = e.jsFolder || !1;
        if (!t) return !1;
        if (r && document.getElementById(r)) return !1;
        var a = document.createElement("script");
        if (void 0 !== i) {
          if ("function" != typeof i) throw new Error("Not a valid callback!");
          a.onload = i;
        }
        return (
          (a.id = r),
          (a.src = e ? s.jsFolder + t : t),
          o && (a.type = o),
          (a.async = !!l),
          (a.defer = !!n),
          d.elBody.appendChild(a),
          !0
        );
      },
      isFuncTrue: async (o) => {
        var i;
        return (
          "function" == typeof o &&
          ((i = 0),
          new Promise((e, t) => {
            var r = setInterval(() => {
              o()
                ? (clearInterval(r), e(!0))
                : 30 < i && (clearInterval(r), t(!0)),
                i++;
            }, 333);
          }).catch((e) => console.log("Function does not exist: " + o)))
        );
      },
      initFunction: (e) => {
        d.elBody.classList.add(e.class),
          l.addEvent(window, e.event),
          (d.events[e.event] = !0);
      },
      runModule: (t) => {
        let e =
            "http:" == window.location.protocol ||
            "https:" == window.location.protocol
              ? "module"
              : "fn",
          r =
            ("fn" ==
            (e =
              !s.jsLoadType ||
              ("fn" != s.jsLoadType && "module" != s.jsLoadType)
                ? e
                : s.jsLoadType)
              ? s.jsFolder
              : "./") +
            e +
            "." +
            t.plugin +
            ".js";
        var o;
        return (
          t.file && (r = t.file),
          "module" == e
            ? import(r)
                .then((e) => e.default(t.selector))
                .catch((e) => {
                  console.log(t.plugin + ": Module could not be loaded"),
                    console.log(e);
                })
            : (o = () => void 0 !== l.getVars.fn[t.plugin])()
            ? l.getVars.fn[t.plugin](t.selector)
            : (l.loadJS({ file: r, id: "canvas-" + t.plugin + "-fn" }),
              l.isFuncTrue(o).then((e) => {
                if (!e) return !1;
                l.getVars.fn[t.plugin](t.selector);
              })),
          !0
        );
      },
      initModule: (t) => {
        if (
          "dependent" != t.selector &&
          ("object" == typeof t.selector
            ? (t.selector instanceof jQuery && (t.selector = t.selector[0]),
              t.selector)
            : l.getVars.baseEl.querySelectorAll(t.selector)
          ).length < 1
        )
          return !1;
        var o,
          i = !0,
          e = !0;
        return (
          t.required &&
            Array.isArray(t.required) &&
            ((o = {}),
            t.required.forEach((e) => (o[e.plugin] = !!e.fn())),
            t.required.forEach((r) => {
              r.fn() ||
                ((i = !1),
                (async function () {
                  l.loadJS({ file: r.file, id: r.id });
                  var e = new Promise((e) => {
                    var t = setInterval(() => {
                      r.fn() &&
                        ((o[r.plugin] = !0),
                        Object.values(o).every((e) => !0 === e)) &&
                        (clearInterval(t), e(!0));
                    }, 333);
                  });
                  (i = await e), l.runModule(t);
                })());
            })),
          void 0 !== t.dependency &&
            "function" == typeof t.dependency &&
            ((e = !1),
            (e = (async function () {
              return new Promise((e) => {
                1 == t.dependency.call(t, "dependent") && e(!0);
              });
            })())),
          i && e && l.runModule(t),
          !0
        );
      },
      topScrollOffset: () => {
        let e = 0,
          t = d.elPageMenu?.querySelector("#page-menu-wrap")?.offsetHeight || 0;
        d.elBody.classList.contains("is-expanded-menu") &&
          (d.elHeader?.classList.contains("sticky-header") &&
            (e = d.elHeaderWrap.offsetHeight),
          d.elPageMenu?.classList.contains("dots-menu")) &&
          (t = 0),
          (e += t),
          (l.getVars.topScrollOffset = e + s.scrollOffset);
      },
    },
    e = {
      init: () => {
        SEMICOLON.Mobile.any() && d.elBody.classList.add("device-touch");
      },
      menuBreakpoint: () => {
        l.getVars.menuBreakpoint <= l.viewport().width
          ? d.elBody.classList.add("is-expanded-menu")
          : d.elBody.classList.remove("is-expanded-menu"),
          d.elPageMenu &&
            (void 0 === l.getVars.pageMenuBreakpoint &&
              (l.getVars.pageMenuBreakpoint = l.getVars.menuBreakpoint),
            l.getVars.pageMenuBreakpoint <= l.viewport().width
              ? d.elBody.classList.add("is-expanded-pagemenu")
              : d.elBody.classList.remove("is-expanded-pagemenu"));
      },
      goToTop: () => l.initModule({ selector: "#gotoTop", plugin: "gototop" }),
      stickFooterOnSmall: () =>
        l.initModule({ selector: "#footer", plugin: "stickfooteronsmall" }),
      logo: () => l.initModule({ selector: "#logo", plugin: "logo" }),
      setHeaderClasses: () => {
        (l.getVars.headerClasses = d.elHeader?.className || ""),
          (l.getVars.headerWrapClasses = d.elHeaderWrap?.className || "");
      },
      headers: () => l.initModule({ selector: "#header", plugin: "headers" }),
      menus: () => l.initModule({ selector: "#header", plugin: "menus" }),
      pageMenu: () =>
        l.initModule({ selector: "#page-menu", plugin: "pagemenu" }),
      sliderDimensions: () =>
        l.initModule({
          selector: ".slider-element",
          plugin: "sliderdimensions",
        }),
      sliderMenuClass: () =>
        l.initModule({
          selector:
            ".transparent-header + .swiper_wrapper,.swiper_wrapper + .transparent-header,.transparent-header + .revslider-wrap,.revslider-wrap + .transparent-header",
          plugin: "slidermenuclass",
        }),
      topSearch: () =>
        l.initModule({ selector: "#top-search-trigger", plugin: "search" }),
      topCart: () => l.initModule({ selector: "#top-cart", plugin: "topcart" }),
      sidePanel: () =>
        l.initModule({ selector: "#side-panel", plugin: "sidepanel" }),
      adaptiveColorScheme: () =>
        l.initModule({
          selector: ".adaptive-color-scheme",
          plugin: "adaptivecolorscheme",
        }),
      portfolioAjax: () =>
        l.initModule({ selector: ".portfolio-ajax", plugin: "ajaxportfolio" }),
      cursor: () => {
        if (d.customCursor)
          return l.initModule({ selector: "body", plugin: "cursor" });
      },
      setBSTheme: () => {
        d.elBody.classList.contains("dark")
          ? document.querySelector("html").setAttribute("data-bs-theme", "dark")
          : (document.querySelector("html").removeAttribute("data-bs-theme"),
            document
              .querySelectorAll(".dark")
              ?.forEach((e) => e.setAttribute("data-bs-theme", "dark"))),
          d.elBody
            .querySelectorAll(".not-dark")
            ?.forEach((e) => e.setAttribute("data-bs-theme", "light"));
      },
    },
    t = {
      easing: () =>
        l.initModule({
          selector: "[data-easing]",
          plugin: "easing",
          required: [d.required.jQuery],
        }),
      bootstrap: () => {
        let t = !0;
        document.querySelectorAll("*").forEach(
          (e) =>
            t &&
            e.getAttributeNames().some((e) => {
              if (e.includes("data-bs"))
                return (
                  (t = !1),
                  l.initModule({ selector: "body", plugin: "bootstrap" })
                );
            })
        );
      },
      resizeVideos: (e) =>
        l.initModule({
          selector:
            e ||
            'iframe[src*="youtube"],iframe[src*="vimeo"],iframe[src*="dailymotion"],iframe[src*="maps.google.com"],iframe[src*="google.com/maps"]',
          plugin: "fitvids",
          required: [d.required.jQuery],
        }),
      pageTransition: () => {
        if (d.pageTransition)
          return l.initModule({ selector: "body", plugin: "pagetransition" });
      },
      lazyLoad: (e) =>
        l.initModule({
          selector: e || ".lazy:not(.lazy-loaded)",
          plugin: "lazyload",
        }),
      dataClasses: () =>
        l.initModule({ selector: "[data-class]", plugin: "dataclasses" }),
      dataHeights: () =>
        l.initModule({
          selector:
            "[data-height-xxl],[data-height-xl],[data-height-lg],[data-height-md],[data-height-sm],[data-height-xs]",
          plugin: "dataheights",
        }),
      lightbox: (e) =>
        l.initModule({
          selector: e || "[data-lightbox]",
          plugin: "lightbox",
          required: [d.required.jQuery],
        }),
      modal: (e) =>
        l.initModule({
          selector: e || ".modal-on-load",
          plugin: "modal",
          required: [d.required.jQuery],
        }),
      parallax: (e) =>
        l.initModule({
          selector: e || ".parallax .parallax-bg,.parallax .parallax-element",
          plugin: "parallax",
        }),
      animations: (e) =>
        l.initModule({ selector: e || "[data-animate]", plugin: "animations" }),
      hoverAnimations: (e) =>
        l.initModule({
          selector: e || "[data-hover-animate]",
          plugin: "hoveranimation",
        }),
      gridInit: (e) =>
        l.initModule({
          selector: e || ".grid-container",
          plugin: "isotope",
          required: [d.required.jQuery],
        }),
      filterInit: (e) =>
        l.initModule({
          selector: e || ".grid-filter,.custom-filter",
          plugin: "gridfilter",
          required: [d.required.jQuery],
        }),
      canvasSlider: (e) =>
        l.initModule({ selector: e || ".swiper_wrapper", plugin: "swiper" }),
      sliderParallax: () =>
        l.initModule({
          selector: ".slider-parallax",
          plugin: "sliderparallax",
        }),
      flexSlider: (e) =>
        l.initModule({
          selector: e || ".fslider",
          plugin: "flexslider",
          required: [d.required.jQuery],
        }),
      html5Video: (e) =>
        l.initModule({ selector: e || ".video-wrap", plugin: "html5video" }),
      youtubeBgVideo: (e) =>
        l.initModule({
          selector: e || ".yt-bg-player",
          plugin: "youtube",
          required: [d.required.jQuery],
        }),
      toggle: (e) =>
        l.initModule({ selector: e || ".toggle", plugin: "toggles" }),
      accordion: (e) =>
        l.initModule({
          selector: e || ".accordion",
          plugin: "accordions",
          required: [d.required.jQuery],
        }),
      counter: (e) =>
        l.initModule({
          selector: e || ".counter",
          plugin: "counter",
          required: [d.required.jQuery],
        }),
      countdown: (e) =>
        l.initModule({
          selector: e || ".countdown",
          plugin: "countdown",
          required: [d.required.jQuery],
        }),
      gmap: (e) =>
        l.initModule({
          selector: e || ".gmap",
          plugin: "gmap",
          required: [d.required.jQuery],
        }),
      roundedSkill: (e) =>
        l.initModule({
          selector: e || ".rounded-skill",
          plugin: "piechart",
          required: [d.required.jQuery],
        }),
      progress: (e) =>
        l.initModule({ selector: e || ".skill-progress", plugin: "progress" }),
      twitterFeed: (e) =>
        l.initModule({
          selector: e || ".twitter-feed",
          plugin: "twitter",
          required: [d.required.jQuery],
        }),
      flickrFeed: (e) =>
        l.initModule({
          selector: e || ".flickr-feed",
          plugin: "flickrfeed",
          required: [d.required.jQuery],
        }),
      instagram: (e) =>
        l.initModule({
          selector: e || ".instagram-photos",
          plugin: "instagram",
        }),
      navTree: (e) =>
        l.initModule({
          selector: e || ".nav-tree",
          plugin: "navtree",
          required: [d.required.jQuery],
        }),
      carousel: (e) =>
        l.initModule({
          selector: e || ".carousel-widget",
          plugin: "carousel",
          required: [d.required.jQuery],
        }),
      masonryThumbs: (e) =>
        l.initModule({
          selector: e || ".masonry-thumbs",
          plugin: "masonrythumbs",
          required: [d.required.jQuery],
        }),
      notifications: (e) =>
        l.initModule({
          selector: e || !1,
          plugin: "notify",
          required: [d.required.jQuery],
        }),
      textRotator: (e) =>
        l.initModule({
          selector: e || ".text-rotater",
          plugin: "textrotator",
          required: [d.required.jQuery],
        }),
      onePage: (e) =>
        l.initModule({
          selector: e || "[data-scrollto],.one-page-menu",
          plugin: "onepage",
        }),
      ajaxForm: (e) =>
        l.initModule({
          selector: e || ".form-widget",
          plugin: "ajaxform",
          required: [d.required.jQuery],
        }),
      subscribe: (e) =>
        l.initModule({
          selector: e || ".subscribe-widget",
          plugin: "subscribe",
          required: [d.required.jQuery],
        }),
      conditional: (e) =>
        l.initModule({
          selector: e || ".form-group[data-condition]",
          plugin: "conditional",
        }),
      shapeDivider: (e) =>
        l.initModule({
          selector: e || ".shape-divider",
          plugin: "shapedivider",
        }),
      stickySidebar: (e) =>
        l.initModule({
          selector: e || ".sticky-sidebar-wrap",
          plugin: "stickysidebar",
          required: [d.required.jQuery],
        }),
      cookies: (e) =>
        l.initModule({
          selector: e || ".gdpr-settings,[data-cookies]",
          plugin: "cookie",
        }),
      quantity: (e) =>
        l.initModule({ selector: e || ".quantity", plugin: "quantity" }),
      readmore: (e) =>
        l.initModule({ selector: e || "[data-readmore]", plugin: "readmore" }),
      pricingSwitcher: (e) =>
        l.initModule({
          selector: e || ".pts-switcher",
          plugin: "pricingswitcher",
        }),
      ajaxButton: (e) =>
        l.initModule({
          selector: e || "[data-ajax-loader]",
          plugin: "ajaxbutton",
        }),
      videoFacade: (e) =>
        l.initModule({ selector: e || ".video-facade", plugin: "videofacade" }),
      schemeToggler: (e) =>
        l.initModule({
          selector: e || ".body-scheme-toggle",
          plugin: "schemetoggler",
        }),
      clipboardCopy: (e) =>
        l.initModule({ selector: e || ".clipboard-copy", plugin: "clipboard" }),
      codeHighlight: (e) =>
        l.initModule({
          selector: e || ".code-highlight",
          plugin: "codehighlight",
        }),
      viewportDetect: (e) =>
        l.initModule({
          selector: e || ".viewport-detect",
          plugin: "viewportdetect",
        }),
      bsComponents: (e) =>
        l.initModule({
          selector:
            e ||
            '[data-bs-toggle="tooltip"],[data-bs-toggle="popover"],[data-bs-toggle="tab"],[data-bs-toggle="pill"],.style-msg',
          plugin: "bscomponents",
        }),
    },
    r = {
      Android: () => navigator.userAgent.match(/Android/i),
      BlackBerry: () => navigator.userAgent.match(/BlackBerry/i),
      iOS: () => navigator.userAgent.match(/iPhone|iPad|iPod/i),
      Opera: () => navigator.userAgent.match(/Opera Mini/i),
      Windows: () => navigator.userAgent.match(/IEMobile/i),
      any: () =>
        r.Android() || r.BlackBerry() || r.iOS() || r.Opera() || r.Windows(),
    },
    o = { onReady: () => {}, onLoad: () => {}, onResize: () => {} },
    i = {
      init: () => {
        (d.resizers.viewport = () => l.viewport()),
          (d.resizers.breakpoints = () => l.breakpoints()),
          (d.resizers.menuBreakpoint = () => e.menuBreakpoint()),
          l.run(d.resizers),
          o.onResize(),
          l.addEvent(window, "cnvsResize");
      },
    },
    n = {
      init: () => {
        l.breakpoints(),
          l.colorScheme(),
          l.runBase(),
          l.runModules(),
          l.topScrollOffset(),
          n.windowscroll(),
          o.onReady();
      },
      windowscroll: () => {
        l.scrollEnd(() => {
          e.pageMenu();
        });
      },
    },
    a = {
      init: () => {
        o.onLoad();
      },
    };
  return (
    document.addEventListener("DOMContentLoaded", () => {
      n.init();
    }),
    (window.onload = () => {
      a.init();
    }),
    l.onResize(() => {
      i.init();
    }),
    { Core: l, Base: e, Modules: t, Mobile: r, Custom: o }
  );
});
