import {
  HStack,
  Input,
  Tab,
  TabList,
  TabPanel,
  TabPanels,
  Tabs,
  useControllableState,
} from "@chakra-ui/react";
import React, { useCallback } from "react";
import { useHistory } from "react-router";
import { Author, Book } from "../@types/api";
import { AddButton } from "../components/AddButton";
import { AllAuthorList } from "../components/AllAuthorList";
import { AllBookList } from "../components/AllBookList";

const BooksTab: React.FC<{
  onAddBook: () => any;
  onBookClick: (book: Book) => any;
}> = ({ onAddBook, onBookClick }) => {
  const [bookSearchName, setBookSearchName] = useControllableState({
    defaultValue: "",
  });
  return (
    <>
      <HStack mb={4} spacing={8}>
        <AddButton mt={4} onClick={onAddBook} />
        <Input
          placeholder="input search book name"
          value={bookSearchName}
          onChange={(e) => setBookSearchName(e.target.value)}
        />
      </HStack>
      <AllBookList onClick={onBookClick} searchName={bookSearchName} />
    </>
  );
};

const AuthorsTab: React.FC<{
  onAddAuthor: () => any;
  onAuthorClick: (author: Author) => any;
}> = ({ onAddAuthor, onAuthorClick }) => {
  const [authorSearchName, setAuthorSearchName] = useControllableState({
    defaultValue: "",
  });

  return (
    <>
      <HStack mb={4} spacing={8}>
        <AddButton mt={4} onClick={onAddAuthor} />
        <Input
          placeholder="input search author name"
          value={authorSearchName}
          onChange={(e) => setAuthorSearchName(e.target.value)}
        />
      </HStack>
      <AllAuthorList onClick={onAuthorClick} searchName={authorSearchName} />
    </>
  );
};

export const IndexPage: React.FC = () => {
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

  return (
    <Tabs isFitted>
      <TabList>
        <Tab>Books</Tab>
        <Tab>Authors</Tab>
      </TabList>
      <TabPanels>
        <TabPanel>
          <BooksTab
            onAddBook={() => history.push("/books/new")}
            onBookClick={onBookClick}
          />
        </TabPanel>
        <TabPanel>
          <AuthorsTab
            onAddAuthor={() => history.push("/authors/new")}
            onAuthorClick={onAuthorClick}
          />
        </TabPanel>
      </TabPanels>
    </Tabs>
  );
};
