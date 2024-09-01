package br.ufal.ic.p2.jackut.Sistema;

import br.ufal.ic.p2.jackut.exceptions.*;
import br.ufal.ic.p2.jackut.modelos.*;
import br.ufal.ic.p2.jackut.modelos.Restaurante;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Sistema {
    int contadorProdutoId = 1;
    int contadorId = 1;
    int contadorIdEmpresa = 1;
    int contadorPedido = 1;
    String pedidoEstado = "aberto";
    Map<String, Usuario> usuariosPorEmail = new HashMap<>();
    Map<Integer, Usuario> usuariosPorId = new HashMap<>();
    Map<Integer, Usuario> donosPorId = new HashMap<>();

    Map<Integer, List<Restaurante>> empresasPorDono = new HashMap<>();
    Map<Integer, Restaurante> empresasPorId = new HashMap<>();
    Map<String, Restaurante> empresasPorNome = new HashMap<>();


    Map<Integer, Produto> produtoPorId = new HashMap<>();


    Map<Integer, Pedidos> pedidosPorCliente = new HashMap<>();
    Map<Integer, Pedidos> pedidosPorNumero = new HashMap<>();



    public void zerarSistema() {
        contadorId = 1;
        contadorIdEmpresa = 1;
        contadorProdutoId = 1;
        contadorPedido = 1;
        usuariosPorEmail.clear();
        usuariosPorId.clear();
        donosPorId.clear();
        empresasPorDono.clear();
        empresasPorId.clear();
        produtoPorId.clear();
        pedidosPorCliente.clear();
        pedidosPorNumero.clear();
    }

    public void criarUsuario(String name, String email, String senha, String address) throws NameValidationException, EmailValidationException, SenhaValidationException, AdressValidationException, EmailFormatValidationException, EmailExistsValidation {
        validarData(name, email, senha, address);
        UsuarioCliente cliente = new UsuarioCliente(contadorId, name, email, senha, address);
        if (!usuariosPorEmail.containsKey(email)) {
            usuariosPorEmail.put(email, cliente);
            usuariosPorId.put(contadorId, cliente);
            contadorId++;
        } else {
            throw new EmailExistsValidation();
        }
    }
    public void criarUsuario(String name, String email, String senha, String address, String cpf) throws NameValidationException, EmailValidationException, SenhaValidationException, AdressValidationException, EmailFormatValidationException, CpfValidationException, EmailExistsValidation {

        validarData(name, email, senha, address);
        validarCpf(cpf);
        if (!usuariosPorEmail.containsKey(email)) {
            UsuarioDono dono = new UsuarioDono(contadorId, name, email, senha, address, cpf);
            usuariosPorEmail.put(email, dono);
            usuariosPorId.put(contadorId, dono);
            donosPorId.put(contadorId, dono);
            contadorId++;
        } else {
            throw new EmailExistsValidation();
        }
    }

    public String getAtributoUsuario(int id, String atributo) throws UserRegistrationValidation {
        //System.out.println(id);
        if (usuariosPorId.containsKey(id)) {
            Usuario usuario = usuariosPorId.get(id);
            if (atributo.equals("cpf")) {
                return ((UsuarioDono) usuario).getCpf();
            }
            switch (atributo) {
                case "nome":
                    return usuario.getName();
                case "email":
                    return usuario.getEmail();
                case "senha":
                    return usuario.getSenha();
                case "endereco":
                    return usuario.getAddress();
                default:
                    throw new UserRegistrationValidation();
            }
        } else {
            throw new UserRegistrationValidation();
        }
    }

    public int login(String email, String senha) throws LoginValidationException, EmailSenhaValidationException {
        Usuario usuario = usuariosPorEmail.get(email);
        if (email == null || email.isEmpty() || !email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$") || senha == null || senha.trim().isEmpty()) {
            throw new EmailSenhaValidationException();
        }
        if (usuario == null) {
            throw new LoginValidationException();
        } else if (usuario.getEmail().equals(email) && usuario.getSenha().equals(senha)) {
            return usuario.getId();
        } else {
            throw new EmailSenhaValidationException();
        }
    }

    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String address, String tipoCozinha) throws UserNotDonoException, EmpresaNomeAddressValidationException, EmpresaNomeValidationException {
        if (!donosPorId.containsKey(dono)) {
            throw new UserNotDonoException();
        }
        for (Map.Entry<Integer, Restaurante> entry : empresasPorId.entrySet()) {

            Restaurante restaurante = entry.getValue();

            if(restaurante.getIdDono() != dono && restaurante.getName().equals(nome)){
                throw new EmpresaNomeValidationException();
            }

            if(restaurante.getIdDono() == dono && restaurante.getName().equals(nome) && restaurante.getAddress().equals(address)){
                throw new EmpresaNomeAddressValidationException();
            }
        }
        Restaurante restaurante = new Restaurante(dono,tipoEmpresa, contadorIdEmpresa, nome, address, tipoCozinha);
        empresasPorId.put(contadorIdEmpresa, restaurante);
        if(empresasPorDono.get(dono) == null){
            empresasPorDono.put(dono,new ArrayList<>());
        }
        else{
            empresasPorDono.get(dono).add(restaurante);
        }
        empresasPorNome.put(nome, restaurante);

        contadorIdEmpresa++;
        return contadorIdEmpresa - 1;
    }
    public String getEmpresasDoUsuario(int idDono) throws UserRegistrationValidation, UserNotDonoException {
        if (!donosPorId.containsKey(idDono)) {
            throw new UserNotDonoException();
        }
        int index = 0;
        int i = 0;
        StringBuilder resultado = new StringBuilder("{[");
        for (Map.Entry<Integer, Restaurante> entry : empresasPorId.entrySet()) {
            Restaurante restaurante = entry.getValue();
            if(restaurante.getIdDono() == idDono){
                index++;
            }
        }
        for (Map.Entry<Integer, Restaurante> entry : empresasPorId.entrySet()) {
            Restaurante restaurante = entry.getValue();
            if(restaurante.getIdDono() == idDono){
                resultado.append("[").append(restaurante.getName()).append(", ").append(restaurante.getAddress()).append("]");
                i ++;

            }
            if(i < index && index != 1){
                resultado.append(", ");
            }
        }
        resultado.append("]}");
        return resultado.toString();
    }

    public String getAtributoEmpresa (int empresa, String atributo) throws AtributoValidationException, EmpresaRegistrationValidation {
        if(!empresasPorId.containsKey(empresa)){
            throw new EmpresaRegistrationValidation();
        }
        if(atributo == null || atributo.isEmpty()){
            throw new AtributoValidationException();
        }
        if (!atributo.equals("nome") && !atributo.equals("endereco") && !atributo.equals("tipoCozinha") && !atributo.equals("dono")) {
            throw new AtributoValidationException();
        }
        Restaurante restaurante = empresasPorId.get(empresa);
        if(atributo.equals("dono")){
            for (Map.Entry<Integer, Usuario> entry : donosPorId.entrySet()) {
                Usuario usuario = entry.getValue();
                if(restaurante.getIdDono() == usuario.getId()){
                    return usuario.getName();
                }
            }
        }
        switch (atributo){
            case "nome":
                return restaurante.getName();
            case "endereco":
                return restaurante.getAddress();
            case "tipoCozinha":
                return restaurante.getTipoCozinha();
        }
        return "";
    }
    public int getIdEmpresa (int idDono, String nome, int indice) throws UserRegistrationValidation, NameValidationException, IndiceValidationException, IndiceLenghtValidationException, EmpresaNameExistsValidationException {
        if(nome == null || nome.isEmpty()){
            throw new NameValidationException();
        }
        if(indice < 0 ){
            throw new IndiceValidationException();
        }
        if(!empresasPorNome.containsKey(nome)){
            throw new EmpresaNameExistsValidationException();
        }
        int index = 0;
        int index2 = 0;
        int retorno = 0;

        for (Map.Entry<Integer, Restaurante> entry : empresasPorId.entrySet()) {
            Restaurante restaurante = entry.getValue();
            if(restaurante.getIdDono() == idDono && restaurante.getName().equals(nome)){
                index++;
            }
        }

        if(indice >= index){
            throw new IndiceLenghtValidationException();
        }

        for (Map.Entry<Integer, Restaurante> entry : empresasPorId.entrySet()) {
            Restaurante restaurante = entry.getValue();
            if(restaurante.getIdDono() == idDono && restaurante.getName().equals(nome)){
                if(index2 == indice){
                    retorno = restaurante.getId();
                    break;
                }
                index2++;
            }
        }
        return retorno;
    }
    public int criarProduto(int empresa, String nome, float valor, String categoria) throws NameValidationException, CategoriaValidationException, ValorValidationException, ProdutoNomeValidationException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new NameValidationException();
        }
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new CategoriaValidationException();
        }
        if(valor <= 0 ){
            throw new ValorValidationException();
        }
        for (Map.Entry<Integer, Produto> entry : produtoPorId.entrySet()) {
            Produto produto = entry.getValue();
            if(produto.getNome().equals(nome) && produto.getIdEmpresa() == empresa){
                throw new ProdutoNomeValidationException();
            }
        }
        Produto produto = new Produto(contadorProdutoId,empresa,nome,valor,categoria);
        produtoPorId.put(contadorProdutoId,produto);
        contadorProdutoId++;

        return contadorProdutoId - 1;
    }
    public void editarProduto(int produto, String nome, float valor, String categoria) throws NameValidationException, CategoriaValidationException, ValorValidationException, ProdutoRegistrationValidation {
        if (nome == null || nome.trim().isEmpty()) {
            throw new NameValidationException();
        }
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new CategoriaValidationException();
        }
        if(valor <= 0 ){
            throw new ValorValidationException();
        }
        if(!produtoPorId.containsKey(produto)){
            throw new ProdutoRegistrationValidation();
        }
        Produto produtoExistente = produtoPorId.get(produto);

        produtoExistente.setNome(nome);
        produtoExistente.setValor(valor);
        produtoExistente.setCategoria(categoria);

        produtoPorId.put(produto, produtoExistente);
    }
    public String getProduto(String  nome, int empresa, String atributo) throws ProductAtributoValidationException, ProductNotFoundException {
        if(atributo == null || atributo.isEmpty()){
            throw new ProductAtributoValidationException();
        }
        if (!atributo.equals("categoria") && !atributo.equals("valor") && !atributo.equals("empresa")) {
            throw new ProductAtributoValidationException();
        }
        String resultado = "";

        for (Map.Entry<Integer, Produto> entry : produtoPorId.entrySet()) {
            Produto produto = entry.getValue();
            if(atributo.equals("categoria")){
                if(produto.getNome().equals(nome) && produto.getIdEmpresa() == empresa){
                    return produto.getCategoria();
                }
            }
            if(atributo.equals("valor")){
                if(produto.getNome().equals(nome) && produto.getIdEmpresa() == empresa){
                    return  String.format(Locale.US, "%.2f", produto.getValor());
                }
            }
            if(atributo.equals("empresa")){
                if(produto.getNome().equals(nome) && produto.getIdEmpresa() == empresa){
                    for (Map.Entry<Integer, Restaurante> entrada : empresasPorId.entrySet()) {
                        Restaurante restaurante = entrada.getValue();
                        if(restaurante.getId() == produto.getIdEmpresa()){
                            return restaurante.getName();
                        }
                    }
                }
            }
        }
        throw new ProductNotFoundException();
    }
    public String listarProdutos(int empresa) throws EmpresaNotFoundException {
        if(!empresasPorId.containsKey(empresa)){
            throw new EmpresaNotFoundException();
        }
        int index = 0;
        int i = 0;
        StringBuilder produtoslista = new StringBuilder();
        for (Map.Entry<Integer, Produto> entry : produtoPorId.entrySet()) {
            Produto produto = entry.getValue();
            if(produto.getIdEmpresa() == empresa){
                index++;
            }
        }
        produtoslista.append("{[");
        for (Map.Entry<Integer, Produto> entry : produtoPorId.entrySet()) {
            Produto produto = entry.getValue();
            if(produto.getIdEmpresa() == empresa){
                i++;
                produtoslista.append(produto.getNome());
            }
            if(i < index && index != 1){
                produtoslista.append(", ");
            }
        }
        produtoslista.append("]}");
        return produtoslista.toString();
    }
    public int criarPedido(int cliente, int empresa) throws DonoPedidoRegistrationException, PedidoEstadoValidationException, PedidoFechadoException {
        if(donosPorId.containsKey(cliente)){
            throw new DonoPedidoRegistrationException();

        }
        for (Map.Entry<Integer, Pedidos> entry : pedidosPorCliente.entrySet()) {
            Pedidos pedidos = entry.getValue();
            if (pedidos.getClienteId() == cliente && pedidos.getEmpresaId() == empresa && pedidos.getEstado().equals("aberto")) {
                throw new PedidoEstadoValidationException();
            }

        }

        String nomeEmpresa = "";
        String nomeCliente = "";
        for (Map.Entry<Integer, Restaurante> entry : empresasPorId.entrySet()) {
            Restaurante restaurante = entry.getValue();
            if(restaurante.getId() == empresa){
                nomeEmpresa = restaurante.getName();
            }
        }
        for (Map.Entry<Integer, Usuario> entry : usuariosPorId.entrySet()) {
            Usuario usuario = entry.getValue();
            if(usuario.getId() == cliente){
                nomeCliente = usuario.getName();
            }
        }
        Pedidos pedidos = new Pedidos(contadorPedido,nomeCliente,nomeEmpresa,pedidoEstado,cliente,empresa);
        pedidosPorCliente.put(cliente,pedidos);
        pedidosPorNumero.put(contadorPedido,pedidos);
        contadorPedido++;
        return contadorPedido-1;
    }
    public void adicionarProduto(int numero, int produto) throws PedidoDoesNotExistsExceptdion, ProdutoEmpresaValidationException, PedidoFechadoException {
        if(!pedidosPorNumero.containsKey(numero)){
            throw new PedidoDoesNotExistsExceptdion();
        }
        Pedidos pedido = pedidosPorNumero.get(numero);
        Produto produtoBase = produtoPorId.get(produto);
        if(!pedido.getEstado().equals("aberto")){
            throw new PedidoFechadoException();
        }
        if (produtoBase.getIdEmpresa() != pedido.getEmpresaId()) {
            throw new ProdutoEmpresaValidationException();
        }
        pedido.adicionarProduto(produtoBase);
    }
    public String getPedidos(int  numero, String atributo) throws Exception {
        if(atributo == null || atributo.isEmpty()){
            throw new AtributoValidationException();
        }
        if(!atributo.equals("cliente") && !atributo.equals("empresa") && !atributo.equals("estado") && !atributo.equals("produtos") && !atributo.equals("valor")){
            throw new AtributoExistsValidationException();
        }

        Pedidos pedido = pedidosPorNumero.get(numero);

        Usuario usuario = usuariosPorId.get(pedido.getClienteId());
        Restaurante restaurante = empresasPorId.get(pedido.getEmpresaId());
        if(atributo.equals("cliente")){
            return usuario.getName();
        }
        if(atributo.equals("empresa")){
            return restaurante.getName();
        }
        if(atributo.equals("estado")){
            return pedido.getEstado();
        }
        if(atributo.equals("valor")){
            return String.format(Locale.US, "%.2f", pedido.getValor());
        }
        if(atributo.equals("produtos")){
            return pedido.getListaDeProdutos();
        }
        return "";
    }
    public void fecharPedido(int numero) throws PedidoNotFoundException {
        if(!pedidosPorNumero.containsKey(numero)){
            throw new PedidoNotFoundException();
        }
        Pedidos pedido = pedidosPorNumero.get(numero);
        pedido.setEstado("preparando");
    }
    public void removerProduto(int pedido,String produto) throws ProdutoValidationException, PedidoRemoverValidationException, ProductNotFoundException {
        if(produto == null || produto.isEmpty()){
            throw new ProdutoValidationException();
        }
        Pedidos pedidoData = pedidosPorNumero.get(pedido);
        if(pedidoData.getEstado().equals("preparando")){
            throw new PedidoRemoverValidationException();
        }
        if(!pedidoData.removerProduto(produto)){
            throw new ProductNotFoundException();

        }
    }
    public int getNumeroPedido(int cliente, int empresa, int indice) {
        int index = 0;
        int pedidoId = 0;

        for (Map.Entry<Integer, Pedidos> entry : pedidosPorNumero.entrySet()) {
            Pedidos pedido = entry.getValue();

            if (pedido.getClienteId() == cliente && pedido.getEmpresaId() == empresa) {
                if (index == indice){
                    pedidoId  = pedido.getNumeroPedido();
                    break;
                }
                index++;
            }
        }
        return pedidoId;
    }



    public void validarData(String name, String email, String senha, String address) throws NameValidationException, EmailValidationException, SenhaValidationException, AdressValidationException, EmailFormatValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new NameValidationException();
        }
        if (email == null ||email.isEmpty()) {
            throw new  EmailValidationException();
        }
        if (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new  EmailValidationException();
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new SenhaValidationException();
        }
        if (address == null || address.trim().isEmpty()) {
            throw new AdressValidationException() ;
        }
    }
    public void validarCpf(String cpf) throws CpfValidationException {
        if (cpf == null || cpf.length()!= 14) {
            throw new CpfValidationException();
        }
    }

}
