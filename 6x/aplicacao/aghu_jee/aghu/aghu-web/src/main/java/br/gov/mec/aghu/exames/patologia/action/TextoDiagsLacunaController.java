package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelGrpDiagLacunas;
import br.gov.mec.aghu.model.AelGrpDiagLacunasId;
import br.gov.mec.aghu.model.AelGrpTxtPadraoDiags;
import br.gov.mec.aghu.model.AelTextoPadraoDiags;
import br.gov.mec.aghu.model.AelTxtDiagLacunas;
import br.gov.mec.aghu.model.AelTxtDiagLacunasId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class TextoDiagsLacunaController extends ActionController {

	private static final long serialVersionUID = 2425182883472997834L;

	private static final String GRUPO_DIAGNOSTICO_LACUNA = "grupoDiagnosticoLacuna";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private Short aelGrpDiagsLacunaLujLuhSeq;

	private Short aelGrpDiagsLacunaLujSeqp;

	private Short aelGrpDiagsLacunaSeqp;

	private List<AelTxtDiagLacunas> lista;

	// Bisavo
	private AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags;

	// Avo
	private AelTextoPadraoDiags aelTextoPadraoDiags;

	// Pai
	private AelGrpDiagLacunas aelGrpDiagLacunas;

	// Filho
	private AelTxtDiagLacunas aelTxtDiagLacunas;

	private boolean editando;
	
	private AelTxtDiagLacunasId idExcluir;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String inicio() {
	 

		
		editando = false;
		this.aelGrpDiagLacunas = this.examesPatologiaFacade.obterAelGrpDiagLacunas(new AelGrpDiagLacunasId(this.aelGrpDiagsLacunaLujLuhSeq,
				this.aelGrpDiagsLacunaLujSeqp, this.aelGrpDiagsLacunaSeqp));

		if(aelGrpDiagLacunas == null){
			apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			return voltar();
		}
		
		this.criarObjetoInsersao();
		this.pesquisar();
		return null;
	
	}

	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelTxtDiagLacunasPorAelGrpDiagLacunas(this.aelGrpDiagLacunas, null);
	}

	public void gravar() {
		try {
			
			final boolean novo = this.aelTxtDiagLacunas.getId() == null || this.aelTxtDiagLacunas.getId().getSeqp() == null;

			this.examesPatologiaFacade.persistirAelTxtDiagLacunas(this.aelTxtDiagLacunas);

			this.apresentarMsgNegocio(Severity.INFO,
					novo ? "MENSAGEM_AEL_TXT_DIAGNOSTICO_LACUNAS_INSERT_SUCESSO" : "MENSAGEM_AEL_TXT_DIAGNOSTICO_LACUNAS_UPDATE_SUCESSO",
					this.aelTxtDiagLacunas.getTextoLacuna());
			if (!novo) {
				this.cancelarEdicao();
			}

			this.criarObjetoInsersao();
		} catch (final BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}

		this.pesquisar();
	}

	public void editar(final AelTxtDiagLacunas aelTxtDiagLacunas) {
		this.editando = true;
		this.aelTxtDiagLacunas = aelTxtDiagLacunas;
	}

	public void cancelarEdicao() {
		this.editando = false;
		this.criarObjetoInsersao();
	}
	
	private void criarObjetoInsersao() {
		this.aelTxtDiagLacunas = new AelTxtDiagLacunas();
		this.aelTxtDiagLacunas.setAelGrpDiagLacunas(this.aelGrpDiagLacunas);
	}
	
	public void excluir() {
		try {
			final AelTxtDiagLacunas aelTxtDiagLacunasExcluir = this.examesPatologiaFacade.obterAelTxtDiagLacunas(idExcluir);
			idExcluir = null;
			
			if(aelTxtDiagLacunasExcluir == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			
			this.examesPatologiaFacade.excluirAelTxtDiagLacunas(aelTxtDiagLacunasExcluir);

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_TXT_DIAGNOSTICO_LACUNAS_DELETE_SUCESSO",
					aelTxtDiagLacunasExcluir.getTextoLacuna());

			this.pesquisar();
		} catch (final BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void ativarInativar(final AelTxtDiagLacunas elemento) {
		try {
			String msg;
			if(DominioSituacao.A.equals(elemento.getIndSituacao())){
				elemento.setIndSituacao(DominioSituacao.I);
				msg = "MENSAGEM_AEL_TXT_DIAGNOSTICO_LACUNAS_INATIVADO_SUCESSO";
				
			} else {
				elemento.setIndSituacao(DominioSituacao.A);
				msg = "MENSAGEM_AEL_TXT_DIAGNOSTICO_LACUNAS_ATIVADO_SUCESSO";
			}
			
			examesPatologiaFacade.persistirAelTxtDiagLacunas(elemento);
			apresentarMsgNegocio(Severity.INFO, msg, elemento.getTextoLacuna());

			this.criarObjetoInsersao();
		} catch (final BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}

		this.pesquisar();
	}

	public String voltar() {
		return GRUPO_DIAGNOSTICO_LACUNA;
	}

	public Short getAelGrpDiagsLacunaLujLuhSeq() {
		return aelGrpDiagsLacunaLujLuhSeq;
	}

	public void setaelGrpDiagsLacunaLujLuhSeq(final Short aelGrpDiagsLacunaLujLuhSeq) {
		this.aelGrpDiagsLacunaLujLuhSeq = aelGrpDiagsLacunaLujLuhSeq;
	}

	public Short getAelGrpDiagsLacunaLujSeqp() {
		return aelGrpDiagsLacunaLujSeqp;
	}

	public void setAelGrpDiagsLacunaLujSeqp(final Short aelGrpDiagsLacunaLujSeqp) {
		this.aelGrpDiagsLacunaLujSeqp = aelGrpDiagsLacunaLujSeqp;
	}

	public Short getAelGrpDiagsLacunaSeqp() {
		return aelGrpDiagsLacunaSeqp;
	}

	public void setAelGrpDiagsLacunaSeqp(final Short aelGrpDiagsLacunaSeqp) {
		this.aelGrpDiagsLacunaSeqp = aelGrpDiagsLacunaSeqp;
	}

	public List<AelTxtDiagLacunas> getLista() {
		return lista;
	}

	public void setLista(final List<AelTxtDiagLacunas> lista) {
		this.lista = lista;
	}

	public AelGrpTxtPadraoDiags getAelGrpTxtPadraoDiags() {
		return aelGrpTxtPadraoDiags;
	}

	public void setAelGrpTxtPadraoDiags(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags) {
		this.aelGrpTxtPadraoDiags = aelGrpTxtPadraoDiags;
	}

	public AelTextoPadraoDiags getAelTextoPadraoDiags() {
		return aelTextoPadraoDiags;
	}

	public void setAelTextoPadraoDiags(final AelTextoPadraoDiags aelTextoPadraoDiags) {
		this.aelTextoPadraoDiags = aelTextoPadraoDiags;
	}

	public AelGrpDiagLacunas getAelGrpDiagLacunas() {
		return aelGrpDiagLacunas;
	}

	public void setAelGrpDiagLacunas(final AelGrpDiagLacunas aelGrpDiagLacunas) {
		this.aelGrpDiagLacunas = aelGrpDiagLacunas;
	}

	public AelTxtDiagLacunas getAelTxtDiagLacunas() {
		return aelTxtDiagLacunas;
	}

	public void setAelTxtDiagLacunas(final AelTxtDiagLacunas aelTxtDiagLacunas) {
		this.aelTxtDiagLacunas = aelTxtDiagLacunas;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(final boolean editando) {
		this.editando = editando;
	}

	public AelTxtDiagLacunasId getIdExcluir() {
		return idExcluir;
	}

	public void setIdExcluir(AelTxtDiagLacunasId idExcluir) {
		this.idExcluir = idExcluir;
	}
}