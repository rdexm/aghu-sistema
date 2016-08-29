package br.gov.mec.aghu.patrimonio.cadastroapoio;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

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
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterEdificacaoController extends ActionController {
	
	private static final long serialVersionUID = -8068792129031822941L;
	private static final Log LOG = LogFactory.getLog(ManterEdificacaoController.class);
	
	private static final String TELA_PESQUISA = "patrimonio-pesquisaEdificacao";
	private double centerLatitude= -30.0385045521296;
	private double centerLongitude= -51.20628833770752;
	
	@Inject
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private IPatrimonioFacade patrimonioFacade;
		
	private AipBairros bairros;
	private AipCepLogradouros cep;
	private AipCidades municipio;
	private AipLogradouros logradouro;
	private AipUfs uf;
	private Integer numero;
	private Integer seq;
	private MapModel mapModel;
	private PtmBemPermanentes patrimonio;
	private String complemento;
	private String descricao;
	private String nome;
	
	private boolean situacao = false;
	private boolean editando = false;
	private Double latitude;
	private Double longitude;
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		this.mapModel = new DefaultMapModel();
	}
	
	public void iniciar(){
		if(this.editando){
			try {

				DadosEdificacaoVO dados = this.patrimonioFacade.obterDadosEdificacaoDAO(seq);
				
				this.nome = dados.getNome();
				this.descricao = dados.getDescricao();
				this.latitude = dados.getLatitude();
				this.longitude = dados.getLongitude();
				this.situacao = dados.getIndSituacao().isAtivo();
				this.numero = dados.getNumero();
				this.complemento = dados.getComplemento();
				
				this.patrimonio = this.patrimonioFacade.obterBemPermanentesPorNumeroBem(dados.getNumeroBem());
				
				List<AipBairrosCepLogradouro> aipBairrosCepLogradouros = this.patrimonioFacade.pesquisarCeps(dados.getCep(), null);
				
				if(aipBairrosCepLogradouros != null && !aipBairrosCepLogradouros.isEmpty()){
					this.cep = aipBairrosCepLogradouros.get(0).getCepLogradouro();
					this.bairros = aipBairrosCepLogradouros.get(0).getAipBairro();
					if(aipBairrosCepLogradouros.get(0).getAipLogradouro() != null){
						logradouro = aipBairrosCepLogradouros.get(0).getAipLogradouro();
						if(aipBairrosCepLogradouros.get(0).getAipLogradouro().getAipCidade() != null){
							this.municipio = aipBairrosCepLogradouros.get(0).getAipLogradouro().getAipCidade();
							this.uf = aipBairrosCepLogradouros.get(0).getAipLogradouro().getAipCidade().getAipUf();
						}
					}
				}
				addMarker();
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage());
				apresentarExcecaoNegocio(e);
			}
		}else{
			this.situacao = true;
		}
	}
	
	public String voltar(){
		this.nome = null;
		this.descricao = null;
		this.cep = null;
		this.uf = null;
		this.municipio = null;
		this.bairros = null;
		this.logradouro = null;
		this.numero = null;
		this.complemento = null;
		this.patrimonio = null;
		this.situacao = false;
		this.mapModel = new DefaultMapModel();
		this.latitude = null;
		this.longitude = null;
		this.seq = null;
		this.editando = false;
		this.centerLatitude= -30.0385045521296;
		this.centerLongitude= -51.20628833770752;
		
		return TELA_PESQUISA;
	}
	
	public String gravar(){
			
		
		if(this.latitude != null && this.longitude != null){
			try {
				if(editando){
					patrimonioFacade.alterarEdificacao(DominioSituacao.getInstance(situacao), nome, descricao, patrimonio.getSeq(), logradouro.getCodigo(), numero, complemento, latitude, longitude, seq);
				}else{
					patrimonioFacade.gravarEdificacao(DominioSituacao.getInstance(situacao), nome, descricao, patrimonio.getSeq(), logradouro.getCodigo(), numero, complemento, latitude, longitude);
				}
				apresentarMsgNegocio(Severity.INFO, "MSG_GRAVADO_SUCESSO");
				return voltar();
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage());
				apresentarExcecaoNegocio(e);
			}
		}else{

			String msg1 = "";
			String msg2 = "";
			
			if(this.latitude == null){
				msg1 = "Latitude: Campo obrigatório, digite um valor.";
				apresentarMsgNegocio(Severity.ERROR, msg1);
			} 
				
			if(this.longitude == null){
				msg2 = "Longitude: Campo obrigatório, digite um valor.";
				apresentarMsgNegocio(Severity.ERROR, msg2);
			}
			
		}
		return null;
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
	
	public void addMarker() {
		Marker marker = new Marker(new LatLng(this.latitude, this.longitude), "Teste");
        if(mapModel.getMarkers() != null && !mapModel.getMarkers().isEmpty()){
        	mapModel.getMarkers().clear();
        	mapModel.addOverlay(marker);
        }else{
        	mapModel.addOverlay(marker);
        }
        this.centerLatitude = this.latitude;
        this.centerLongitude = this.longitude;
    }
	
	public String getCepFormatado(Integer cep){
		return CoreUtil.formataCEP(cep);
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
		return patrimonioFacade.obterPtmBemPermanentesPorNumeroDescricaoCount(filtro);
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
	public PtmBemPermanentes getPatrimonio() {
		return patrimonio;
	}

	public void setPatrimonio(PtmBemPermanentes patrimonio) {
		this.patrimonio = patrimonio;
	}
	
	public boolean isSituacao() {
		return situacao;
	}

	public void setSituacao(boolean situacao) {
		this.situacao = situacao;
	}

	public MapModel getMapModel() {
		return mapModel;
	}

	public void setMapModel(MapModel mapModel) {
		this.mapModel = mapModel;
	}

	public double getCenterLatitude() {
		return centerLatitude;
	}

	public double getCenterLongitude() {
		return centerLongitude;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public void setCenterLatitude(double centerLatitude) {
		this.centerLatitude = centerLatitude;
	}

	public void setCenterLongitude(double centerLongitude) {
		this.centerLongitude = centerLongitude;
	}

	public static Log getLog() {
		return LOG;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}