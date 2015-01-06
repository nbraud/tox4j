#pragma once

#include "Packet/decoder.h"
#include "Packet/encoder.h"

#include <limits>


namespace tox
{
  enum class PacketKind
    : byte
  {
    PingRequest         = 0x00,
    PingResponse        = 0x01,

    NodesRequest        = 0x02,
    NodesResponse       = 0x04,

    CookieRequest       = 0x18,
    CookieResponse      = 0x19,

    CryptoHandshake     = 0x1a,
    CryptoData          = 0x1b,
    Crypto              = 0x20,

    LANDiscovery        = 0x21,
  };


  CipherText &operator << (CipherText &packet, PacketKind kind);
  BitStream<CipherText> operator >> (BitStream<CipherText> const &packet, PacketKind &kind);


  template<PacketKind Kind, typename ...Contents>
  using PacketFormat = PacketFormatTag<
    std::integral_constant<PacketKind, Kind>,
    Contents...
  >;


  template<typename Format, typename ArgsTuple = typename detail::packet_arguments<Format>::type>
  struct PacketBase;

  template<typename Format, typename... Args>
  struct PacketBase<Format, std::tuple<Args...>>
    : detail::packet_encoder<Format, std::tuple<Args...>>
    , detail::packet_decoder<Format, std::tuple<Args...>>
  {
    typedef detail::packet_encoder<Format, std::tuple<Args...>> encoder_type;
    typedef detail::packet_decoder<Format, std::tuple<Args...>> decoder_type;

    typedef typename decoder_type::decoder decoder;


    explicit PacketBase (Args const &...args)
    {
      this->encode (packet_, args...);
    }

    explicit PacketBase (CipherText const &packet)
      : packet_ (packet)
    { }


    decoder decode () const
    {
      CipherText packet = CipherText::from_bytes (data (), size ());
      return decoder_type::decode (std::move (packet));
    }

    decoder decode (CryptoBox const &box) const
    {
      CipherText packet = CipherText::from_bytes (data (), size ());
      return decoder_type::decode (std::move (packet), box);
    }


    byte const *data () const { return packet_.data (); }
    std::size_t size () const { return packet_.size (); }

  private:
    CipherText packet_;
  };


  template<typename Format>
  using Packet = PacketBase<Format>;
}
