package lofimodding.gradient.utils;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolderRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class RegistryObject2<T extends IForgeRegistryEntry<? super T>> implements Supplier<T> {
  private final ResourceLocation name;
  @Nullable
  private T value;

  public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject2<U> of(final ResourceLocation name, final Supplier<IForgeRegistry<T>> registry) {
    return new RegistryObject2<>(name, registry);
  }

  private static final RegistryObject2<?> EMPTY = new RegistryObject2<>();

  private static <T extends IForgeRegistryEntry<? super T>> RegistryObject2<T> empty() {
    @SuppressWarnings("unchecked") final RegistryObject2<T> t = (RegistryObject2<T>)EMPTY;
    return t;
  }

  private RegistryObject2() {
    this.name = null;
  }

  @SuppressWarnings("unchecked")
  private <V extends IForgeRegistryEntry<V>> RegistryObject2(final ResourceLocation name, final Supplier<IForgeRegistry<V>> registry) {
    this.name = name;
    ObjectHolderRegistry.addHandler(pred -> {
      final IForgeRegistry<V> r = registry.get();

      if(r == null) {
        throw new IllegalArgumentException("Invalid registry argument, must not be null");
      }

      if(pred.test(r.getRegistryName())) {
        this.value = r.containsKey(this.name) ? (T)r.getValue(this.name) : null;
      }
    });
  }

  /**
   * Directly retrieves the wrapped Registry Object. This value will automatically be updated when the backing registry is updated.
   * Will throw NPE if the value is null, use isPresent to check first. Or use any of the other guarded functions.
   */
  @Override
  @Nonnull
  public T get() {
    final T ret = this.value;
    Objects.requireNonNull(ret, () -> "Registry Object not present: " + this.name);
    return ret;
  }

  public void updateReference(final IForgeRegistry<? extends T> registry) {
    this.value = registry.getValue(this.getId());
  }

  public ResourceLocation getId() {
    return this.name;
  }

  public Stream<T> stream() {
    return this.isPresent() ? Stream.of(this.get()) : Stream.of();
  }

  /**
   * Return {@code true} if there is a mod object present, otherwise {@code false}.
   *
   * @return {@code true} if there is a mod object present, otherwise {@code false}
   */
  public boolean isPresent() {
    return this.value != null;
  }

  /**
   * If a mod object is present, invoke the specified consumer with the object,
   * otherwise do nothing.
   *
   * @param consumer block to be executed if a mod object is present
   *
   * @throws NullPointerException if mod object is present and {@code consumer} is
   *                              null
   */
  public void ifPresent(final Consumer<? super T> consumer) {
    if(this.isPresent()) {
      consumer.accept(this.get());
    }
  }

  /**
   * If a mod object is present, and the mod object matches the given predicate,
   * return an {@code RegistryObject} describing the value, otherwise return an
   * empty {@code RegistryObject}.
   *
   * @param predicate a predicate to apply to the mod object, if present
   *
   * @return an {@code RegistryObject} describing the value of this {@code RegistryObject}
   * if a mod object is present and the mod object matches the given predicate,
   * otherwise an empty {@code RegistryObject}
   *
   * @throws NullPointerException if the predicate is null
   */
  public RegistryObject2<T> filter(final Predicate<? super T> predicate) {
    Objects.requireNonNull(predicate);
    if(!this.isPresent()) {
      return this;
    } else {
      return predicate.test(this.get()) ? this : empty();
    }
  }

  /**
   * If a mod object is present, apply the provided mapping function to it,
   * and if the result is non-null, return an {@code Optional} describing the
   * result.  Otherwise return an empty {@code Optional}.
   *
   * @param <U>    The type of the result of the mapping function
   * @param mapper a mapping function to apply to the mod object, if present
   *
   * @return an {@code Optional} describing the result of applying a mapping
   * function to the mod object of this {@code RegistryObject}, if a mod object is present,
   * otherwise an empty {@code Optional}
   *
   * @throws NullPointerException if the mapping function is null
   * @apiNote This method supports post-processing on optional values, without
   * the need to explicitly check for a return status.
   */
  public <U> Optional<U> map(final Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper);
    if(!this.isPresent()) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(mapper.apply(this.get()));
    }
  }

  /**
   * If a value is present, apply the provided {@code Optional}-bearing
   * mapping function to it, return that result, otherwise return an empty
   * {@code Optional}.  This method is similar to {@link #map(Function)},
   * but the provided mapper is one whose result is already an {@code Optional},
   * and if invoked, {@code flatMap} does not wrap it with an additional
   * {@code Optional}.
   *
   * @param <U>    The type parameter to the {@code Optional} returned by
   * @param mapper a mapping function to apply to the mod object, if present
   *               the mapping function
   *
   * @return the result of applying an {@code Optional}-bearing mapping
   * function to the value of this {@code Optional}, if a value is present,
   * otherwise an empty {@code Optional}
   *
   * @throws NullPointerException if the mapping function is null or returns
   *                              a null result
   */
  public <U> Optional<U> flatMap(final Function<? super T, Optional<U>> mapper) {
    Objects.requireNonNull(mapper);
    if(!this.isPresent()) {
      return Optional.empty();
    } else {
      return Objects.requireNonNull(mapper.apply(this.get()));
    }
  }

  /**
   * If a mod object is present, lazily apply the provided mapping function to it,
   * returning a supplier for the transformed result. If this object is empty, or the
   * mapping function returns {@code null}, the supplier will return {@code null}.
   *
   * @param <U>    The type of the result of the mapping function
   * @param mapper A mapping function to apply to the mod object, if present
   *
   * @return A {@code Supplier} lazily providing the result of applying a mapping
   * function to the mod object of this {@code RegistryObject}, if a mod object is present,
   * otherwise a supplier returning {@code null}
   *
   * @throws NullPointerException if the mapping function is {@code null}
   * @apiNote This method supports post-processing on optional values, without
   * the need to explicitly check for a return status.
   */
  public <U> Supplier<U> lazyMap(final Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper);
    return () -> this.isPresent() ? mapper.apply(this.get()) : null;
  }

  /**
   * Return the mod object if present, otherwise return {@code other}.
   *
   * @param other the mod object to be returned if there is no mod object present, may
   *              be null
   *
   * @return the mod object, if present, otherwise {@code other}
   */
  public T orElse(final T other) {
    return this.isPresent() ? this.get() : other;
  }

  /**
   * Return the mod object if present, otherwise invoke {@code other} and return
   * the result of that invocation.
   *
   * @param other a {@code Supplier} whose result is returned if no mod object
   *              is present
   *
   * @return the mod object if present otherwise the result of {@code other.get()}
   *
   * @throws NullPointerException if mod object is not present and {@code other} is
   *                              null
   */
  public T orElseGet(final Supplier<? extends T> other) {
    return this.isPresent() ? this.get() : other.get();
  }

  /**
   * Return the contained mod object, if present, otherwise throw an exception
   * to be created by the provided supplier.
   *
   * @param <X>               Type of the exception to be thrown
   * @param exceptionSupplier The supplier which will return the exception to
   *                          be thrown
   *
   * @return the present mod object
   *
   * @throws X                    if there is no mod object present
   * @throws NullPointerException if no mod object is present and
   *                              {@code exceptionSupplier} is null
   * @apiNote A method reference to the exception constructor with an empty
   * argument list can be used as the supplier. For example,
   * {@code IllegalStateException::new}
   */
  public <X extends Throwable> T orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X {
    if(this.isPresent()) {
      return this.get();
    } else {
      throw exceptionSupplier.get();
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if(this == obj) {
      return true;
    }
    if(obj instanceof RegistryObject2) {
      return Objects.equals(((RegistryObject2<?>)obj).name, this.name);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.name);
  }
}
