package br.gov.mec.aghu.financeiro.centrocusto.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioArea;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDespesa;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FcuGrupoCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CentroCustoController extends ActionController {

	private static final long serialVersionUID = -2046065959820903870L;

	private static final String CENTRO_CUSTO_LIST = "centroCustoList";

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	private ICadastrosBasicosPacienteFacade cadastroBasicoPacienteFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;
	
	private FccCentroCustos centroCusto;
	
	private FccCentroCustos centroCustoAntigo;
	
	private Integer codigoCentroCusto;
	
	private DominioSimNao possuiAreaFisica;
	
	private Boolean isEdicao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
		

		if (codigoCentroCusto != null) {
			this.centroCusto = centroCustoFacade.obterCentroCusto(codigoCentroCusto);
			this.setCentroCustoAntigo(centroCustoFacade.obterCentroCusto(codigoCentroCusto));

			if(centroCusto == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}

			if(centroCusto.getIndPossuiAreaFisica() != null){
				if(centroCusto.getIndPossuiAreaFisica()){
					this.setPossuiAreaFisica(DominioSimNao.S);
				}
				else{
					this.setPossuiAreaFisica(DominioSimNao.N);
				}
			}

		} else {
			this.centroCusto = new FccCentroCustos();

			this.centroCusto.setIndArea(DominioArea.G);
			this.centroCusto.setIndSituacao(DominioSituacao.A);
			this.centroCusto.setIndSolicitaCompra(false);
			this.centroCusto.setIndAvaliacaoTecnica(false);
			this.centroCusto.setIndAprovaSolicitacao(false);
			this.centroCusto.setIndAbsentSmo(false);
			this.centroCusto.setIndPossuiAreaFisica(false);
			this.possuiAreaFisica = DominioSimNao.N;
		}

		return null;
	
	}

	public String confirmar() {
		
		try {

			if(this.possuiAreaFisica != null){
				if(this.possuiAreaFisica == DominioSimNao.S){
					centroCusto.setIndPossuiAreaFisica(true);
				}
				else{
					centroCusto.setIndPossuiAreaFisica(false);
				}
			}
			
			centroCustoFacade.persistirCentroCusto(centroCusto, isEdicao);

			if (isEdicao) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ALTERAR_CENTRO_CUSTO",centroCusto.getCodigo());
				
			} else {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_INCLUIR_CENTRO_CUSTO", centroCusto.getCodigo());

			}

			return cancelar();
			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String cancelar(){
		codigoCentroCusto = null;
		centroCusto = null;
		return CENTRO_CUSTO_LIST;
	}

	public void atualizarCampoArea(){
		if(DominioSimNao.N.equals(possuiAreaFisica)){
			centroCusto.setArea(null);
		}
	}
	
	public List<SigCentroProducao> getListaCentroProducao() {
		return this.custosSigCadastrosBasicosFacade.pesquisarCentroProducao();
	}
	
	public List<FccCentroCustos> pesquisarCentroCustosSuperior(final String strPesquisa) {
		return this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustosSuperior((String)strPesquisa),pesquisarCentroCustosSuperiorCount(strPesquisa));
	}
	
	public Long pesquisarCentroCustosSuperiorCount(final String strPesquisa) {
		return centroCustoFacade.pesquisarCentroCustosSuperiorCount((String)strPesquisa);
	}

	public List<FcuGrupoCentroCustos> pesquisarGrupoCentroCustos(final String strPesquisa) {
		return this.returnSGWithCount(centroCustoFacade.pesquisarGruposCentroCustos((String)strPesquisa),pesquisarGrupoCentroCustosCount(strPesquisa));
	}

	public Long pesquisarGrupoCentroCustosCount(final String strPesquisa) {
		return centroCustoFacade.pesquisarGruposCentroCustosCount((String)strPesquisa);
	}
	
	public List<RapServidores> pesquisarServidores(String strPesquisa) {
		return this.returnSGWithCount(registroColaboradorFacade.pesquisarRapServidores(strPesquisa),pesquisarServidorCount(strPesquisa));
	}

	public Long pesquisarServidorCount(String strPesquisa) {
		return registroColaboradorFacade.pesquisarRapServidoresCount(strPesquisa);
	}
	
	public List<AipCidades> pesquisarCidadePorCodigoNome(String paramPesquisa) {
		return cadastroBasicoPacienteFacade.pesquisarPorCodigoNome(paramPesquisa, true);
	}
	
	/**
	 * Método que avalia se o tipo de despesa do centro de custo é "Obra"
	 */
	public boolean isTipoDespesaObra() {
		return centroCusto != null  ? (this.centroCusto.getIndTipoDespesa() == DominioTipoDespesa.O) : false;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public DominioSimNao getPossuiAreaFisica() {
		return possuiAreaFisica;
	}

	public void setPossuiAreaFisica(DominioSimNao possuiAreaFisica) {
		this.possuiAreaFisica = possuiAreaFisica;
	}

	public Boolean getIsEdicao() {
		return isEdicao;
	}

	public void setIsEdicao(Boolean isEdicao) {
		this.isEdicao = isEdicao;
	}

	public FccCentroCustos getCentroCustoAntigo() {
		return centroCustoAntigo;
	}

	public void setCentroCustoAntigo(FccCentroCustos centroCustoAntigo) {
		this.centroCustoAntigo = centroCustoAntigo;
	}
}