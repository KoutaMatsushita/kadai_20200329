import {Button, Input, InputProps, Text, VStack} from "@chakra-ui/react";
import React, {useCallback, useState} from "react";

export interface AutoCompleteInputItem {
  id: string | number
  text: string
}

export interface AutoCompleteInputProps extends InputProps {
  autoCompleteItems: AutoCompleteInputItem[]
  onSelectItem: (item: AutoCompleteInputItem) => void
}

export const AutoCompleteInput: React.FC<AutoCompleteInputProps> = ({autoCompleteItems, onSelectItem, ...props}) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const _onSelectItem = useCallback((item: AutoCompleteInputItem) => {
    onSelectItem(item);
  }, [onSelectItem])

  return (
    <>
    <Input
      {...props}
      type="text"
      onClick={() => setIsMenuOpen(true)}
      onBlur={() => setTimeout(() => setIsMenuOpen(false), 200)}
    />
      {
        isMenuOpen &&
        <VStack
          align="stretch"
          w={140}
          maxH="240px"
          py={2}
          border="1px"
          borderRadius="8px"
          borderColor="gray.200"
          overflowY="scroll"
        >
          {
            autoCompleteItems.map(item => <Button
              key={item.id}
              variant="ghost"
              borderRadius={0}
              fontWeight="normal"
              minH="40px"
              onClick={() => _onSelectItem(item)}
            >
              <Text w="100%" textAlign="left">{item.text}</Text>
            </Button>)
          }
        </VStack>
      }
    </>
  )
}