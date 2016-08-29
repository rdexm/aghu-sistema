package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatCbos;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedimentoCbo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class RelacionarCboComProcedimentoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3711816399287304487L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	// recebidos na url
	private Integer iphSeq;
	private Short iphPhoSeq;
	private Integer codigoItem;
	private Integer seqNaTabela;

	private FatItensProcedHospitalar itemProcedHosp;

	private List<FatProcedimentoCbo> procedimentos;

	private Integer cboSeq;

	private FatCbos cbo;

	public enum RelacionarCboComProcedimentoControllerExceptionCode implements BusinessExceptionCode {
		CBO_JA_ASSOCIADO, PROCED_CBO_GRAVADOS_SUCESSO, PROCED_CBO_REMOVIDO_SUCESSO, MENSAGEM_CBO_INVALIDO;
	}

	@PostConstruct
	public void inicializar() {
		begin(conversation);
	}
	
	public void iniciar() {
	 

		this.itemProcedHosp = this.faturamentoFacade.obterItemProcedimentoHospitalar(this.iphSeq, this.iphPhoSeq);
		this.procedimentos = (ArrayList<FatProcedimentoCbo>) this.faturamentoFacade.listarProcedimentoCboPorIphSeqEPhoSeq(this.iphSeq, this.iphPhoSeq);
		this.cbo = null;
	
	}

	public void editar(Integer cboSeq) {
		this.cboSeq = cboSeq;
	}

	public void remover() {
		final FatProcedimentoCbo cboAhExluir = faturamentoFacade.obterProcedimentoCboPorChavePrimaria(cboSeq);
		this.faturamentoFacade.removerProcedimentoCbo(cboAhExluir);
		this.apresentarMsgNegocio(Severity.INFO, RelacionarCboComProcedimentoControllerExceptionCode.PROCED_CBO_REMOVIDO_SUCESSO.toString());
		iniciar();
	}

	public void adicionar() {
		if (cbo == null) {
			if (cbo == null) {
				this.apresentarMsgNegocio(Severity.ERROR, RelacionarCboComProcedimentoControllerExceptionCode.MENSAGEM_CBO_INVALIDO.toString());
				return;
			}
		}

		boolean existe = false;
		try {
			if (this.procedimentos != null && !this.procedimentos.isEmpty()) {
				for (FatProcedimentoCbo elemento : this.procedimentos) {
					if (CoreUtil.igual(elemento.getCbo().getSeq(), cbo.getSeq())) {
						existe = true;
						break;
					}
				}
			}

			if (!existe) {
				final FatProcedimentoCbo procCbo = new FatProcedimentoCbo();
				procCbo.setCbo(this.cbo);
				procCbo.setItemProcedHosp(this.itemProcedHosp);

				gravar(procCbo);
				iniciar();
			} else {
				throw new ApplicationBusinessException(RelacionarCboComProcedimentoControllerExceptionCode.CBO_JA_ASSOCIADO);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void gravar(final FatProcedimentoCbo procCbo) {
		this.faturamentoFacade.inserirProcedimentoCbo(procCbo);
		this.apresentarMsgNegocio(Severity.INFO, RelacionarCboComProcedimentoControllerExceptionCode.PROCED_CBO_GRAVADOS_SUCESSO.toString());
	}

	public List<FatCbos> listarCbos(String objPesquisa) {
		try {
			return this.returnSGWithCount(this.faturamentoFacade.listarCbos(objPesquisa),listarCbosCount(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<FatCbos>();
	}

	public Long listarCbosCount(String objPesquisa) {
		return this.faturamentoFacade.listarCbosCount(objPesquisa);
	}

	public String cancelar() {

		this.iphSeq = null;
		this.iphPhoSeq = null;
		this.codigoItem = null;
		this.seqNaTabela = null;
		this.itemProcedHosp = null;
		this.procedimentos = null;
		this.cboSeq = null;
		this.cbo = null;

		return "faturamento-manterItemPrincipal";
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public Integer getCodigoItem() {
		return codigoItem;
	}

	public void setCodigoItem(Integer codigoItem) {
		this.codigoItem = codigoItem;
	}

	public Integer getSeqNaTabela() {
		return seqNaTabela;
	}

	public void setSeqNaTabela(Integer seqNaTabela) {
		this.seqNaTabela = seqNaTabela;
	}

	public FatItensProcedHospitalar getItemProcedHosp() {
		return itemProcedHosp;
	}

	public void setItemProcedHosp(FatItensProcedHospitalar itemProcedHosp) {
		this.itemProcedHosp = itemProcedHosp;
	}

	public FatCbos getCbo() {
		return cbo;
	}

	public void setCbo(FatCbos cbo) {
		this.cbo = cbo;
	}

	public Integer getCboSeq() {
		return cboSeq;
	}

	public void setCboSeq(Integer cboSeq) {
		this.cboSeq = cboSeq;
	}

	public List<FatProcedimentoCbo> getProcedimentos() {
		return procedimentos;
	}

	public void setProcedimentos(List<FatProcedimentoCbo> procedimentos) {
		this.procedimentos = procedimentos;
	}
}