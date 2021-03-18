import { Tab, TabList, TabPanel, TabPanels, Tabs } from "@chakra-ui/react";
import React, { useCallback } from "react";
import { useHistory } from "react-router";
import useSWR from "swr";
import { fetcher } from "util/fetcher";
import { Author, Book } from "../@types/api";
import { AllAuthorList } from "../components/AllAuthorList";
import { AllBookList } from "../components/AllBookList";
import { LargeProgress } from "../components/LargeProgress";

export const IndexPage: React.FC = () => {
  const { data } = useSWR<Book[]>("/api/books", fetcher);
  const history = useHistory();
  const onBookClick = useCallback(
    (book: Book) => {
      history.push(`/books/${book.id}`);
    },
    [history]
  );
  const onAuthorClick = useCallback(
    (author: Author) => {
      history.push(`/authors/${author.id}`);
    },
    [history]
  );

  if (!data) {
    return <LargeProgress />;
  }

  return (
    <Tabs isFitted>
      <TabList>
        <Tab>Books</Tab>
        <Tab>Authors</Tab>
      </TabList>
      <TabPanels>
        <TabPanel>
          <AllBookList onClick={onBookClick} />
        </TabPanel>
        <TabPanel>
          <AllAuthorList onClick={onAuthorClick} />
        </TabPanel>
      </TabPanels>
    </Tabs>
  );
};
