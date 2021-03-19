import { AddIcon } from "@chakra-ui/icons";
import { Button, ButtonProps } from "@chakra-ui/react";
import React from "react";

export const AddButton: React.FC<ButtonProps> = (props) => (
  <Button
    {...props}
    leftIcon={<AddIcon />}
    mb={4}
    colorScheme="teal"
    variant="outline"
  >
    Add
  </Button>
);
