package com.ebookstore.user.data.repository;

import com.ebookstore.user.data.remote.ApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class BookRepository_Factory implements Factory<BookRepository> {
  private final Provider<ApiService> apiServiceProvider;

  public BookRepository_Factory(Provider<ApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public BookRepository get() {
    return newInstance(apiServiceProvider.get());
  }

  public static BookRepository_Factory create(Provider<ApiService> apiServiceProvider) {
    return new BookRepository_Factory(apiServiceProvider);
  }

  public static BookRepository newInstance(ApiService apiService) {
    return new BookRepository(apiService);
  }
}
