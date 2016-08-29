package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioDiaSemanaMes;
import br.gov.mec.aghu.dominio.DominioIndFrequenciaEntrega;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoParamProgEntgAf;
import br.gov.mec.aghu.model.ScoParamProgEntgAfId;
import br.gov.mec.aghu.model.ScoScJn;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ProgEntregaCompraController extends ActionController {

	private static final long serialVersionUID = -5861595457653656517L;
	private static final Log LOG = LogFactory.getLog(ProgEntregaCompraController.class);

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	// Objetos
	private List<ScoScJn> listFasesSolicitacao;

	private ScoFaseSolicitacao dadosLicitacao = new ScoFaseSolicitacao();

	// Numero da Solicitacao de Compra
	private Integer numero;

	// Solicitacao de Compra
	private ScoSolicitacaoDeCompra solicitacaoDeCompra;

	private DominioIndFrequenciaEntrega indFreqEntrega;

	// Solicitacao de Compra
	private ScoParamProgEntgAf programacaoEntrega;

	private String voltarPara;

	public void iniciar() {
		ScoSolicitacaoDeCompra slc = popularSolicitacaoCompra(this.numero);
		slc.getMaterial().getUnidadeMedida();
		slc.getMaterial().getGrupoMaterial();
		setSolicitacaoDeCompra(slc);
		ScoParamProgEntgAf pe = popularProgEntrega(this.numero);
		if (pe != null) {
			programacaoEntrega = pe;
			bindZero(pe);
		} else {
			programacaoEntrega = new ScoParamProgEntgAf();
		}
	}
	
	private void bindZero(ScoParamProgEntgAf pe) {
		if(pe!=null){
			if(pe.getFreqDias()!=null && pe.getFreqDias() == 0){
				pe.setFreqDias(null);
			}
			if(pe.getQtdeParcela()!=null && pe.getQtdeParcela() == 0){
				pe.setQtdeParcela(null);
			}
			if(pe.getNroParcelas()!=null && pe.getNroParcelas() == 0){
				pe.setNroParcelas(null);
			}
		}
	}

	// BUSCA SOLICITACAO DE COMPRA POR NUMERO
	public ScoSolicitacaoDeCompra popularSolicitacaoCompra(Integer numero) {
		return solicitacaoComprasFacade.obterSolicitacaoDeCompra(numero);
	}

	// BUSCA SUGESTÃO POR SOLICITACAO DE COMPRA
	public ScoParamProgEntgAf popularProgEntrega(Integer numero) {
		return solicitacaoComprasFacade.obterScoParamProgEntgAfPorSolicitacaoDeCompra(numero);
	}

	// EXCLUIR SUGESTAO DE PROGRAMAÇÃO
	public void excluirProgEntrega(ScoParamProgEntgAf programacaoEntrega) {
		solicitacaoComprasFacade.excluirScoParamProgEntgAf(programacaoEntrega);
	}

	// INSERIR/ATUALIZAR UMA SUGESTAO DE PROGRAMAÇÃO
	public Boolean persistirProgEntrega(ScoParamProgEntgAf programacaoEntrega) {
		programacaoEntrega.setScoSolicitacaoDeCompra(this.solicitacaoDeCompra);
		try {
			Boolean inserir = false;
			if (programacaoEntrega.getId() == null) {
				inserir = true;
				ScoParamProgEntgAfId id = new ScoParamProgEntgAfId();
				id.setSeq((short) 1);
				id.setSlcNumero(this.numero);
				programacaoEntrega.setId(id);
			} else if (programacaoEntrega.getId().getSlcNumero() == null) {
				inserir = true;
				programacaoEntrega.getId().setSeq((short) 1);
				programacaoEntrega.getId().setSlcNumero(this.numero);
			}
			solicitacaoComprasFacade.persistirScoParamProgEntgAf(programacaoEntrega, inserir);
		} catch (ApplicationBusinessException e) {
			LOG.error(e, e.getCause());
			apresentarExcecaoNegocio(e);
			return false;
		}
		return true;
	}

	public String voltar() {
		return voltarPara;
	}

	public Long countFasesSolicitacao() {
		return solicitacaoComprasFacade.countPesquisaFasesSolicitacaoCompra(numero);
	}

	// @Restrict("#{s:hasPermission('manterSugestaoProgEntrega', 'gravar')}")
	public void gravar() {
		Boolean ret = persistirProgEntrega(this.programacaoEntrega);
		if (ret) {
			this.apresentarMsgNegocio(Severity.INFO, "MANTER_SUG_PROG_ENTREGA_M1");
		}
	}

	// @Restrict("#{s:hasPermission('manterSugestaoProgEntrega', 'limpar')}")
	public void limpar() {
		if (this.programacaoEntrega.getId() != null) {
			excluirProgEntrega(this.programacaoEntrega);
			this.apresentarMsgNegocio(Severity.INFO, "MANTER_SUG_PROG_ENTREGA_M2");
		}
		this.programacaoEntrega = new ScoParamProgEntgAf();
	}

	// ### GETs e SETs ###

	public void setListFasesSolicitacao(List<ScoScJn> listFasesSolicitacao) {
		this.listFasesSolicitacao = listFasesSolicitacao;
	}

	public List<ScoScJn> getListFasesSolicitacao() {
		return listFasesSolicitacao;
	}

	public ScoFaseSolicitacao getDadosLicitacao() {
		return dadosLicitacao;
	}

	public void setDadosLicitacao(ScoFaseSolicitacao dadosLicitacao) {
		this.dadosLicitacao = dadosLicitacao;
	}

	public void setNumero(final Integer numero) {
		this.numero = numero;
	}

	public Integer getNumero() {
		return numero;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoDeCompra() {
		return solicitacaoDeCompra;
	}

	public void setSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra) {
		this.solicitacaoDeCompra = solicitacaoDeCompra;
	}

	public ScoParamProgEntgAf getProgramacaoEntrega() {
		return programacaoEntrega;
	}

	public void setProgramacaoEntrega(ScoParamProgEntgAf programacaoEntrega) {
		this.programacaoEntrega = programacaoEntrega;
	}

	public DominioIndFrequenciaEntrega getIndFreqEntregaCombo(String valor) {
		return DominioIndFrequenciaEntrega.getInstance(valor);
	}

	public String getDescricaoDominioIndFrequenciaEntrega(String valor) {
		return DominioIndFrequenciaEntrega.getInstance(valor).getDescricao();
	}

	public Integer getDominioDiaSemanaMesCombo(String valor) {
		return DominioDiaSemanaMes.getInstance(valor).getCodigo();
	}

	public String getDominioDiaSemanaMes(String valor) {
		return DominioDiaSemanaMes.getInstance(valor).getDescricao();
	}

	public Integer getDominioDiaSemanaCombo(String valor) {
		return DominioDiaSemana.getInstance(valor).getNumeroDiaDaSemana();
	}

	public String getDominioDiaSemana(String valor) {
		return DominioDiaSemana.getInstance(valor).getDescricaoCompleta();
	}

	public DominioIndFrequenciaEntrega getIndFreqEntrega() {
		return indFreqEntrega;
	}

	public void setIndFreqEntrega(DominioIndFrequenciaEntrega indFreqEntrega) {
		this.indFreqEntrega = indFreqEntrega;
	}

	public Boolean getExibirDiaSemana() {
		if (programacaoEntrega != null) {
			if (programacaoEntrega.getIndFreqEntrega() != null) {
				if (programacaoEntrega.getIndFreqEntrega().name().equals(DominioIndFrequenciaEntrega.S.name())) {
					return true;
				} else if (programacaoEntrega.getIndFreqEntrega().name().equals(DominioIndFrequenciaEntrega.D.name())) {
					programacaoEntrega.setDiaEntrega(null);
				}
			}
		}
		return false;
	}

	public Boolean getExibirDiaMes() {
		if (programacaoEntrega != null) {
			if (programacaoEntrega.getIndFreqEntrega() != null) {
				if (programacaoEntrega.getIndFreqEntrega().name().equals(DominioIndFrequenciaEntrega.M.name())) {
					return true;
				} else if (programacaoEntrega.getIndFreqEntrega().name().equals(DominioIndFrequenciaEntrega.D.name())) {
					programacaoEntrega.setDiaEntrega(null);
				}
			}
		}
		return false;
	}

}
