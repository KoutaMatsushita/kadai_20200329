import { extendTheme } from "@chakra-ui/react";

const theme = extendTheme({
  config: {
    initialColorMode: "light",
    useSystemColorMode: true,
  },
  components: {
    Link: {
      baseStyle: {
        _hover: {
          color: "teal.500",
        },
      },
    },
  },
});

export default theme;
