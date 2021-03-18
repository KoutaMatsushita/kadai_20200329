import {Center, Spinner} from "@chakra-ui/react";
import React from "react";

export const LargeProgress: React.FC = () => (
  <Center h="100%">
    <Spinner
      thickness="4px"
      speed="1s"
      size="xl"
    />
  </Center>
)