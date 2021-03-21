import { StackDivider, Text, VStack } from "@chakra-ui/react";
import React, { useCallback } from "react";
import useSWR from "swr";
import { Book } from "../@types/api";
import { fetcher } from "../util/fetcher";
import { LargeProgress } from "./LargeProgress";

const BookItem: React.FC<{ book: Book; onClick: (book: Book) => any }> = ({
  book,
  onClick,
}) => {
  const _onClick = useCallback(() => {
    onClick(book);
  }, [book, onClick]);

  return (
    <VStack
      align="stretch"
      padding={2}
      _hover={{
        color: "teal.500",
        cursor: "pointer",
      }}
      role="group"
      onClick={_onClick}
    >
      <Text>{book.name}</Text>
      <Text fontSize="sm" color="gray.400" _groupHover={{ color: "teal.400" }}>
        written by {book.author.name}
      </Text>
    </VStack>
  );
};

export const AllBookList: React.FC<{
  onClick: (book: Book) => any;
  searchName?: string;
}> = ({ onClick, searchName }) => {
  const { data } = useSWR<Book[]>(
    searchName ? `/api/books?q=${searchName}` : "/api/books",
    fetcher
  );

  if (!data) {
    return <LargeProgress />;
  }

  return (
    <VStack align="stretch" divider={<StackDivider borderColor="gray.100" />}>
      {data.map((book) => (
        <BookItem key={book.id} book={book} onClick={onClick} />
      ))}
    </VStack>
  );
};
