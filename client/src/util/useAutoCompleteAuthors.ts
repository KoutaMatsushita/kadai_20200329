import { useControllableState } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import useSWR from "swr";
import { Fetcher } from "swr/dist/types";
import { Author } from "../@types/api";
import { AutoCompleteInputItem } from "../components/AutoCompleteInput";

export interface useAutoCompleteAuthorsArgs {
  defaultValue: string;
  fetcher: Fetcher<Author[]>;
}

export const useAutoCompleteAuthors = ({
  fetcher,
  defaultValue,
}: useAutoCompleteAuthorsArgs) => {
  const [authorName, setAuthorName] = useControllableState({ defaultValue });
  const { data: authors } = useSWR<Author[]>(
    `/api/authors?q=${authorName}`,
    fetcher
  );
  const [autoCompleteAuthors, setAutoCompleteAuthors] = useState<
    AutoCompleteInputItem[]
  >([]);
  const _authors = authors || [];

  useEffect(() => {
    const items = _authors.map((author: Author) => ({
      id: author.id,
      text: author.name,
    }));
    setAutoCompleteAuthors(items);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(_authors), authorName]);

  return {
    authorName,
    setAuthorName,
    autoCompleteAuthors,
  };
};
