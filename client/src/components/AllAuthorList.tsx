import { StackDivider, Text, VStack } from "@chakra-ui/react";
import React, { useCallback } from "react";
import useSWR from "swr";
import { Author, Book } from "../@types/api";
import { fetcher } from "../util/fetcher";
import { LargeProgress } from "./LargeProgress";

const AuthorItem: React.FC<{
  author: Author;
  onClick: (author: Author) => any;
}> = ({ author, onClick }) => {
  const _onClick = useCallback(() => {
    onClick(author);
  }, [author.id]);

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

export const AllAuthorList: React.FC<{ onClick: (author: Author) => any }> = ({
  onClick,
}) => {
  const { data } = useSWR<Author[]>("/api/authors", fetcher);
  if (!data) {
    return <LargeProgress />;
  }

  return (
    <VStack align="stretch" divider={<StackDivider borderColor="gray.100" />}>
      {data.map((author) => (
        <AuthorItem author={author} onClick={onClick} />
      ))}
    </VStack>
  );
};
