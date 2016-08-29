package br.gov.mec.aghu.patrimonio.cadastroapoio;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipBairros;
import br.gov.mec.aghu.model.AipBairrosCepLogradouro;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.DadosEdificacaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class PesquisaEdificacaoController extends ActionController implements ActionPaginator{

	private static final long serialVersionUID = 8795939019247654011L;
	
	private static final String TELA_MANTER = "patrimonio-manterEdificacao";
	
	@Inject
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private IPatrimonioFacade patrimonioFacade;
		
	private String nome;
	private String descricao;
	private AipCepLogradouros cep;
	private AipUfs uf;
	private AipCidades municipio;
	private AipBairros bairros;
	private AipLogradouros logradouro;
	private Integer numero;
	private String complemento;
	private DominioSituacao situacaoSelecionado;
	private PtmBemPermanentes patrimonio;
	
	private boolean pesquisaAtiva = false;
	
	private List<AipUfs> listaUfs;
	
	@Inject @Paginator
	private DynamicDataModel<DadosEdificacaoVO> dataModel;	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		listaUfs = pacienteFacade.obterAipUfsPorSiglaNome(null);
	}	
	
	@Override
	public Long recuperarCount() {
		return patrimonioFacade.obterListaDadosEdificacaoDAOCount(nome, descricao, logradouro, situacaoSelecionado, 
				municipio, uf, bairros, numero, complemento, cep, logradouro, patrimonio);
	}
	
	public void pesquisar(){
		pesquisaAtiva = true;
		dataModel.reiniciarPaginator();
	}

	@Override
	public List<DadosEdificacaoVO> recuperarListaPaginada(Integer arg0, Integer arg1, String arg2, boolean arg3) {
		
		List<DadosEdificacaoVO> listaRetorno = this.patrimonioFacade.obterListaDadosEdificacaoDAO(nome, descricao, logradouro, situacaoSelecionado, municipio, 
				uf, bairros, numero, complemento, cep, logradouro,patrimonio, arg0, arg1, arg2, arg3);
		pesquisaAtiva = true;

		return listaRetorno;
	}
	
	public String limpar(){
		
		this.nome = null;
		this.descricao = null;
		this.cep = null;
		this.uf = null;
		this.municipio = null;
		this.bairros = null;
		this.logradouro = null;
		this.numero = null;
		this.complemento = null;
		this.situacaoSelecionado = null;
		this.patrimonio = null;
		this.pesquisaAtiva = false;
		this.dataModel.reiniciarPaginator();
		
		return "patrimonio-pesquisaEdificacao";
	}
	
	public void posSelectionSBCep() throws ApplicationBusinessException{
		List<AipBairrosCepLogradouro> aipBairrosCepLogradouro = this.patrimonioFacade.pesquisarCeps(this.cep.getId().getCep(), null);
		
		if(aipBairrosCepLogradouro != null && !aipBairrosCepLogradouro.isEmpty()){

			bairros = aipBairrosCepLogradouro.get(0).getAipBairro();
			if(aipBairrosCepLogradouro.get(0).getAipLogradouro() != null){
				logradouro = aipBairrosCepLogradouro.get(0).getAipLogradouro();
				if(aipBairrosCepLogradouro.get(0).getAipLogradouro().getAipCidade() != null){
					municipio = aipBairrosCepLogradouro.get(0).getAipLogradouro().getAipCidade();
					uf = aipBairrosCepLogradouro.get(0).getAipLogradouro().getAipCidade().getAipUf();
				}
			}
		}
	}
	
	public void posDeleteSB(){
		bairros = null;
		cep = null;
		logradouro = null;
		uf = null;
		municipio = null;
	}
	
	public String redirecionar(){
		return TELA_MANTER;
	}
	
	public List<AipCidades> obterAipCidadesPorNomeAtivo(String filtro){
		return returnSGWithCount(pacienteFacade.obterAipCidadesPorNomeAtivo(filtro), obterAipCidadesPorNomeAtivoCount(filtro));
	}

	private Long obterAipCidadesPorNomeAtivoCount(String filtro){
		return pacienteFacade.obterAipCidadesPorNomeAtivoCount(filtro);
	}
	
	public List<AipLogradouros> obterAipLogradourosPorNome(String filtro){
		return returnSGWithCount(pacienteFacade.obterAipLogradourosPorNome(filtro), obterAipLogradourosPorNomeCount(filtro));
	}
		
	private Long obterAipLogradourosPorNomeCount(String filtro){
		return pacienteFacade.obterAipLogradourosPorNomeCount(filtro);
	}
	
	public List<AipCepLogradouros> obterAipCepLogradourosPorCEP(String filtro) {
		String auxParam = filtro;
		auxParam = StringUtils.remove(auxParam, ".");
		auxParam = StringUtils.remove(auxParam, "_");
		auxParam = StringUtils.remove(auxParam, "-");
		
		return returnSGWithCount(pacienteFacade.obterAipCepLogradourosPorCEP(auxParam), obterAipCepLogradourosPorCEPCount(auxParam));
	}

	private Long obterAipCepLogradourosPorCEPCount(String filtro) {
		return pacienteFacade.obterAipCepLogradourosPorCEPCount(filtro);
	}
	
	public List<AipBairros> obterAipBairrosPorDescricao(String filtro) {
		return returnSGWithCount(pacienteFacade.obterAipBairrosPorDescricao(filtro), obterAipBairrosPorDescricaoCount(filtro));
	}
	
	private Long obterAipBairrosPorDescricaoCount(String filtro) {
		return pacienteFacade.obterAipBairrosPorDescricaoCount(filtro);
	}

	public List<AipUfs> obterAipUfsPorSiglaNome(String filtro) {
		return returnSGWithCount(pacienteFacade.obterAipUfsPorSiglaNome(filtro), obterAipUfsPorSiglaNomeCount(filtro));
	}

	private Long obterAipUfsPorSiglaNomeCount(String filtro) {
		return pacienteFacade.obterAipUfsPorSiglaNomeCount(filtro);
	}
	
	public List<PtmBemPermanentes> obterPtmBemPermanentesPorNumeroDescricao(
			String filtro) {
		return returnSGWithCount(patrimonioFacade.obterPtmBemPermanentesPorNumeroDescricao(filtro), obterPtmBemPermanentesPorNumeroDescricaoCount(filtro));
	}

	private Long obterPtmBemPermanentesPorNumeroDescricaoCount(String filtro) {
		return patrimonioFacade
				.obterPtmBemPermanentesPorNumeroDescricaoCount(filtro);
	}

	public String truncarHint(Object item, Integer tamanhoMaximo) {
		
		String hintText = item.toString();
		
		if (hintText.length() > tamanhoMaximo) {

			return StringUtils.abbreviate(hintText, tamanhoMaximo);

		}

		return hintText;

	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public AipCepLogradouros getCep() {
		return cep;
	}
	public void setCep(AipCepLogradouros cep) {
		this.cep = cep;
	}
	public AipUfs getUf() {
		return uf;
	}
	public void setUf(AipUfs uf) {
		this.uf = uf;
	}
	public AipCidades getMunicipio() {
		return municipio;
	}
	public void setMunicipio(AipCidades municipio) {
		this.municipio = municipio;
	}
	public AipBairros getBairros() {
		return bairros;
	}
	public void setBairros(AipBairros bairros) {
		this.bairros = bairros;
	}
	public AipLogradouros getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(AipLogradouros logradouro) {
		this.logradouro = logradouro;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public DominioSituacao getSituacaoSelecionado() {
		return situacaoSelecionado;
	}
	public void setSituacaoSelecionado(DominioSituacao situacaoSelecionado) {
		this.situacaoSelecionado = situacaoSelecionado;
	}

	public PtmBemPermanentes getPatrimonio() {
		return patrimonio;
	}

	public void setPatrimonio(PtmBemPermanentes patrimonio) {
		this.patrimonio = patrimonio;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public List<AipUfs> getListaUfs() {
		return listaUfs;
	}

	public void setListaUfs(List<AipUfs> listaUfs) {
		this.listaUfs = listaUfs;
	}

	public DynamicDataModel<DadosEdificacaoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<DadosEdificacaoVO> dataModel) {
		this.dataModel = dataModel;
	}
}