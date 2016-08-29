package br.gov.mec.aghu.faturamento.action;

import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatEspelhoSismama;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class FatCaracteristicasFinanciamentoController extends ActionController{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1732158498114283641L;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		limparInclusao();
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private FatCaractFinanciamento cadastro = new FatCaractFinanciamento();

	private Boolean situacao;

	private static final String PAGE_PESQUISA_CARACTERISTICAS_FINANCIAMENTO = "caracteristicasFinanciamentoList";


	public void limparInclusao(){
		cadastro = new FatCaractFinanciamento();
		this.situacao = true; // situação vem checkada
	}

	
	/**
	 * Ação Gravar
	 */
	public String gravar() {
		this.cadastro.setIndSituacao(DominioSituacao.getInstance(this.situacao));
		try {
			this.faturamentoFacade.persistirCaracteristicasFinanciamento(cadastro);

			if (cadastro.getSeq() != null) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_FAT_CARACT_FINANCIAMENTO", cadastro.getDescricao());
			}

			limparInclusao();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Método que realiza a ação do botão voltar
	 */
	public String voltar() {

		limparInclusao();
		cadastro.setFatEspelhoSismamas(new HashSet<FatEspelhoSismama>());
		cadastro.setFatItensProcedimentosHospitalares(new HashSet<FatItensProcedHospitalar>());
		
		return PAGE_PESQUISA_CARACTERISTICAS_FINANCIAMENTO;
	}

	public FatCaractFinanciamento getCadastro() {
		return cadastro; 
	}

	public void setCadastro(FatCaractFinanciamento cadastro) {
		this.cadastro = cadastro;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}


}
