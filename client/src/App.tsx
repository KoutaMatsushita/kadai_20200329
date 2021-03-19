import {
  Box,
  Container,
  Heading,
  StackDivider,
  VStack,
} from "@chakra-ui/react";
import { IndexPage } from "pages";
import React from "react";
import {
  BrowserRouter as Router,
  Link as RouterLink,
  Route,
  Switch,
} from "react-router-dom";
import { NotFoundPage } from "./pages/404";
import { AuthorShowPage } from "./pages/authors/show";
import { BookNewPage } from "./pages/books/new";
import { BookShowPage } from "./pages/books/show";

function App() {
  return (
    <Router>
      <VStack
        divider={<StackDivider borderColor="gray.200" />}
        spacing={4}
        paddingStart={8}
        paddingEnd={8}
        paddingTop={4}
        paddingBottom={4}
        h="100%"
        align="stretch"
      >
        <Box>
          <RouterLink to="/">
            <Heading size="2xl">Syoseki</Heading>
          </RouterLink>
        </Box>
        <Container maxW="container.xl" h="100%">
          <Switch>
            <Route exact path="/" children={<IndexPage />} />
            <Route exact path="/books/new" children={<BookNewPage />} />
            <Route path="/books/:id" children={<BookShowPage />} />
            <Route path="/authors/:id" children={<AuthorShowPage />} />
            <Route path="*" children={<NotFoundPage />} />
          </Switch>
        </Container>
      </VStack>
    </Router>
  );
}

export default App;
