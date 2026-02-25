package com.ebookstore.user.ui.screens.bookshelf;

import com.ebookstore.user.data.repository.BookRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class BookshelfViewModel_Factory implements Factory<BookshelfViewModel> {
  private final Provider<BookRepository> bookRepositoryProvider;

  public BookshelfViewModel_Factory(Provider<BookRepository> bookRepositoryProvider) {
    this.bookRepositoryProvider = bookRepositoryProvider;
  }

  @Override
  public BookshelfViewModel get() {
    return newInstance(bookRepositoryProvider.get());
  }

  public static BookshelfViewModel_Factory create(Provider<BookRepository> bookRepositoryProvider) {
    return new BookshelfViewModel_Factory(bookRepositoryProvider);
  }

  public static BookshelfViewModel newInstance(BookRepository bookRepository) {
    return new BookshelfViewModel(bookRepository);
  }
}
