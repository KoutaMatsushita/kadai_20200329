import { StackDivider, Text, VStack } from "@chakra-ui/react";
import React, { useCallback } from "react";
import useSWR from "swr";
import { Author } from "../@types/api";
import { fetcher } from "../util/fetcher";
import { LargeProgress } from "./LargeProgress";

const AuthorItem: React.FC<{
  author: Author;
  onClick: (author: Author) => any;
}> = ({ author, onClick }) => {
  const _onClick = useCallback(() => {
    onClick(author);
  }, [author, onClick]);

  return (
    <Text
      padding={2}
      onClick={_onClick}
      _hover={{ color: "teal.500", cursor: "pointer" }}
    >
      {author.name}
    </Text>
  );
};

export const AllAuthorList: React.FC<{
  onClick: (author: Author) => any;
  searchName?: string;
}> = ({ onClick, searchName }) => {
  const { data } = useSWR<Author[]>(
    searchName ? `/api/authors?q=${searchName}` : "/api/authors",
    fetcher
  );
  if (!data) {
    return <LargeProgress />;
  }

  return (
    <VStack align="stretch" divider={<StackDivider borderColor="gray.100" />}>
      {data.map((author) => (
        <AuthorItem key={author.id} author={author} onClick={onClick} />
      ))}
    </VStack>
  );
};
