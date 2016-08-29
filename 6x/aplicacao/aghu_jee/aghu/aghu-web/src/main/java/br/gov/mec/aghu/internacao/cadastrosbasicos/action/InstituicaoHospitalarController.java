package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class InstituicaoHospitalarController  extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 642695735967587295L;
	

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	private AipCidades cidade;
	
	private AipUfs uf;

	private AghInstituicoesHospitalares instituicao;

	private Integer instituicaoCodigo;	
	

	private Integer codigoCidadeLov;
	private List<AipCidades> listaCidadesPesq;
	private String descricaoCidadeBuscaLov;
	
	private String codigoUFLov;
	private List<AipUfs> listaUFsPesq;
	private String descricaoUFBuscaLov;
	
	private final String PAGE_LIST_INST_HOSP = "instituicaoHospitalarList";

	/*public String iniciarInclusao() {
		this.instituicao = new AghInstituicoesHospitalares();
		this.instituicao = null;
		this.instituicaoCodigo = null;
		this.codigoUFLov = null;
		this.codigoUFLov = null;
		this.descricaoUFBuscaLov = null;
		this.descricaoCidadeBuscaLov = null;
		this.listaUFsPesq = null;
		this.listaCidadesPesq = null;
		return "iniciarInclusao";
	}*/

	/*@PostConstruct
	public void init() {
		begin(conversation);	
		instituicao = new AghInstituicoesHospitalares();
				
	}*/
	
	@PostConstruct
	public void init() {
		this.setInstituicao(new AghInstituicoesHospitalares());
	}
	
	public void inicio() {
	 
		
				
		if (this.instituicao.getSeq() == null) {			
			this.instituicao.setIndLocal(Boolean.FALSE);
		}
	
	}

	 public void buscaCidade(){
		if (codigoCidadeLov != null) {
			instituicao.setCddCodigo(cadastrosBasicosPacienteFacade
					.obterCidadePorCodigo(this.codigoCidadeLov));
		} else {
			instituicao = new AghInstituicoesHospitalares();
		}
	}
		 
	 
	 public void buscaUF(){
		 if(codigoUFLov != null){
			instituicao.setUfSigla(cadastrosBasicosPacienteFacade.obterUfSemLike(codigoUFLov));
			if(instituicao.getUfSigla() == null){
				apresentarMsgNegocio(Severity.ERROR, "ERRO_UF_INEXISTENTE");
			}
		 }else{
			instituicao = new AghInstituicoesHospitalares();
		 }
	 }
	 
	 public boolean isMostrarLinkExcluirCidade(){
			return instituicao.getCddCodigo() != null;
	}
	 
	 public void limparCidade() {
		 this.instituicao.setCddCodigo(null);
		 this.cidade = null;
	 }
	 
	 public void pesquisaCidade(){	 
		 this.setListaCidadesPesq(cadastrosBasicosPacienteFacade.pesquisarCidades(descricaoCidadeBuscaLov));	 
	 }
	 
	public List<AipCidades> pesquisaCidadeSB(String strPesquisa){	 
		return cadastrosBasicosPacienteFacade.pesquisarCidadePorCodigoNome((String) strPesquisa) ;
	}

	public List<AipCidades> pesquisarCidade(Object nome) {		 	
		return cadastrosBasicosPacienteFacade.pesquisarCidadePorNome((String) nome);
			
	}
	 
	public List<AipUfs> pesquisaUfSB(String strPesquisa){	 
			
		return cadastrosBasicosPacienteFacade.pesquisarPorSiglaNome(strPesquisa) ;
	
	}
	public void pesquisaUF(){	 
		this.setListaUFsPesq(cadastrosBasicosPacienteFacade.pesquisarPorSiglaNome(descricaoUFBuscaLov));
		 
	}
	 
	 
	public String getDescricaoUFSelecionada() {
		if (this.instituicao.getCddCodigo() == null
				|| StringUtils.isBlank(this.instituicao.getCddCodigo().getNome()
						)) {
			return "";
		}
		return this.instituicao.getCddCodigo().getAipUf().getSigla();
	}
	 
	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * acomodação
	 */
	public String confirmar() {

		try {
			// Tarefa 659 - deixar todos textos das entidades em caixa alta via
			// toUpperCase()
			transformarTextosCaixaAlta();

			boolean create = this.instituicao.getSeq() == null;
			
			this.cadastrosBasicosInternacaoFacade.persistirInstituicao(this.instituicao);
			
			// FR this.cadastrosBasicosInternacaoFacade.flush();

			if (create) {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CRIACAO_INSTITUICAO", this.instituicao.getNome());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_INSTITUICAO", this.instituicao.getNome());
			}
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);

			return null;
		}		
		return cancelar();
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * Instituição
	 */
	public String cancelar() {
		//info("Cancelado");
		this.instituicao = null;
		this.instituicaoCodigo = null;
		this.codigoUFLov = null;
		this.codigoUFLov = null;
		this.descricaoUFBuscaLov = null;
		this.descricaoCidadeBuscaLov = null;
		this.listaUFsPesq = null;
		this.listaCidadesPesq = null;
		//reiniciarPaginator(InstituicaoHospitalarPaginatorController.class);
		this.setInstituicao(new AghInstituicoesHospitalares());
		
		return PAGE_LIST_INST_HOSP;
	}

	private void transformarTextosCaixaAlta() {
		this.instituicao.setNome(this.instituicao.getNome() == null ? null
				: this.instituicao.getNome().toUpperCase());
		this.instituicao.setCidade(this.instituicao.getCidade() == null ? null 
				: this.instituicao.getCidade().toUpperCase());
	}



	public AghInstituicoesHospitalares getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(AghInstituicoesHospitalares instituicao) {
		this.instituicao = instituicao;
	}

	public Integer getInstituicaoCodigo() {
		return instituicaoCodigo;
	}

	public void setInstituicaoCodigo(Integer instituicaoCodigo) {
		this.instituicaoCodigo = instituicaoCodigo;
	}

	public void setCodigoCidade(Integer codigoCidade) {
		this.codigoCidadeLov = codigoCidade;
	}

	public Integer getCodigoCidade() {
		return codigoCidadeLov;
	}

	public Integer getCodigoCidadeLov() {
		return codigoCidadeLov;
	}

	public void setCodigoCidadeLov(Integer codigoCidadeLov) {
		this.codigoCidadeLov = codigoCidadeLov;
	}

	public void setCidade(AipCidades cidade) {
		this.cidade = cidade;
	}

	public AipCidades getCidade() {
		return cidade;
	}

	public void setListaCidadesPesq(List<AipCidades> listaCidadesPesq) {
		this.listaCidadesPesq = listaCidadesPesq;
	}

	public List<AipCidades> getListaCidadesPesq() {
		return listaCidadesPesq;
	}

	public void setDescricaoCidadeBuscaLov(String descricaoCidadeBuscaLov) {
		this.descricaoCidadeBuscaLov = descricaoCidadeBuscaLov;
	}

	public String getDescricaoCidadeBuscaLov() {
		return descricaoCidadeBuscaLov;
	}

	public void setCodigoUFLov(String codigoUFLov) {
		this.codigoUFLov = codigoUFLov;
	}

	public String getCodigoUFLov() {
		return codigoUFLov;
	}

	public void setListaUFsPesq(List<AipUfs> listaUFsPesq) {
		this.listaUFsPesq = listaUFsPesq;
	}

	public List<AipUfs> getListaUFsPesq() {
		return listaUFsPesq;
	}

	public void setDescricaoUFBuscaLov(String descricaoUFBuscaLov) {
		this.descricaoUFBuscaLov = descricaoUFBuscaLov;
	}

	public String getDescricaoUFBuscaLov() {
		return descricaoUFBuscaLov;
	}

	public void setUf(AipUfs uf) {
		this.uf = uf;
	}

	public AipUfs getUf() {
		return uf;
	}
	

}
