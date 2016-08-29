package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcGrupoAlcadaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcGrupoAlcadaAvalOpmsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcGrupoAlcadaJnDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoConvenioOpms;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpmsJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MbcGrupoAlcadaRN2 extends BaseBusiness {

	private static final long serialVersionUID = -6320806656607859532L;
	
	private static final Log LOG = LogFactory.getLog(MbcGrupoAlcadaRN2.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MbcGrupoAlcadaDAO mbcGrupoAlcadaDAO;
	
	@Inject
	private MbcGrupoAlcadaJnDAO mbcGrupoAlcadaJnDAO;
	
	@Inject
	private MbcGrupoAlcadaAvalOpmsDAO mbcGrupoAlcadaAvalOpmsDAO;
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;
	
	protected enum MbcGrupoAlcadaRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_EXCLUSAO_ALCADA_JA_UTILIZADA
	}

	public MbcGrupoAlcadaAvalOpms persistirMbcGrupoAlcada(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms, RapServidores rapServidores) {
		
		if (grupoAlcadaAvalOpms.getSeq() == null) {
			Short maxVersao = this.mbcGrupoAlcadaDAO.buscaMaxVersaoGrupoAlcada(
					grupoAlcadaAvalOpms.getTipoConvenio(), grupoAlcadaAvalOpms.getAghEspecialidades());
			
			grupoAlcadaAvalOpms.setVersao(++maxVersao); 
			
			grupoAlcadaAvalOpms.setRapServidores(rapServidores);
			grupoAlcadaAvalOpms.setCriadoEm(new Date());
			//mbcGrupoAlcadaDAO.desatachar(grupoAlcadaAvalOpms);
			
			this.mbcGrupoAlcadaDAO.persistir(grupoAlcadaAvalOpms);
			flush();
			
			this.posInserirMbcGrupoAlcadaAvalOpms(grupoAlcadaAvalOpms);
			
		} else {
			grupoAlcadaAvalOpms.setModificadoEm(new Date());
			grupoAlcadaAvalOpms.setRapServidoresModificacao(rapServidores);

			//grupoAlcadaAvalOpms = this.mbcGrupoAlcadaDAO.merge(grupoAlcadaAvalOpms);
			this.mbcGrupoAlcadaDAO.atualizar(grupoAlcadaAvalOpms);
			this.posAtualizarMbcGrupoAlcadaAvalOpms(grupoAlcadaAvalOpms);
			flush();
		}

		return grupoAlcadaAvalOpms;
	}
	
	protected void posInserirMbcGrupoAlcadaAvalOpms(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms) {
		inserirJournal(grupoAlcadaAvalOpms, DominioOperacoesJournal.INS);
	}
	
	protected void posAtualizarMbcGrupoAlcadaAvalOpms(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms) {
		MbcGrupoAlcadaAvalOpms oldGupoAlcadaAvalOpms = this.getMbcGrupoAlcadaDAO().obterOriginal(grupoAlcadaAvalOpms);

		if(CoreUtil.modificados(grupoAlcadaAvalOpms.getSituacao(), oldGupoAlcadaAvalOpms.getSituacao()) ||
				CoreUtil.modificados(grupoAlcadaAvalOpms.getTipoObrigatoriedade(), oldGupoAlcadaAvalOpms.getTipoObrigatoriedade())){
			inserirJournal(grupoAlcadaAvalOpms, DominioOperacoesJournal.UPD);
		}
	}
	
	protected void inserirJournal(MbcGrupoAlcadaAvalOpms elemento, DominioOperacoesJournal operacao) {
		MbcGrupoAlcadaAvalOpmsJn journal = BaseJournalFactory.getBaseJournal(operacao, MbcGrupoAlcadaAvalOpmsJn.class, obterLoginUsuarioLogado());

		journal.setSeq(elemento.getSeq());
		journal.setTipoConvenio(elemento.getTipoConvenio());
		journal.setAghEspecialidades(elemento.getAghEspecialidades());
		journal.setTipoObrigatoriedade(elemento.getTipoObrigatoriedade());
		journal.setVersao(elemento.getVersao());
		journal.setSituacao(elemento.getSituacao());
		journal.setCriadoEm(elemento.getCriadoEm());
		journal.setModificadoEm(elemento.getModificadoEm());
		journal.setRapServidores(elemento.getRapServidores());
		journal.setRapServidoresModificacao(elemento.getRapServidoresModificacao());	
		
		this.mbcGrupoAlcadaJnDAO.persistir(journal);
		flush();
	}

	protected MbcGrupoAlcadaDAO getMbcGrupoAlcadaDAO() {
		return mbcGrupoAlcadaDAO;
	}
	
	public MbcGrupoAlcadaAvalOpms buscaGrupoAlcada(
			DominioTipoConvenioOpms tipoConvenio,
			AghEspecialidades aghEspecialidades) {
		return getMbcGrupoAlcadaDAO().buscaGrupoAlcada(tipoConvenio,
				aghEspecialidades);
	}

	public MbcGrupoAlcadaAvalOpms alteraGrupoAnterior(
			MbcGrupoAlcadaAvalOpms grupoAlcadaVersaoAnterior,
			RapServidores rapServidores) throws ApplicationBusinessException {
		if (grupoAlcadaVersaoAnterior != null) {
			grupoAlcadaVersaoAnterior.setSituacao(DominioSituacao.I);
			this.getMbcGrupoAlcadaDAO().persistir(grupoAlcadaVersaoAnterior);
			flush();
		}
		return grupoAlcadaVersaoAnterior;
	}

	public void alteraSituacao(MbcGrupoAlcadaAvalOpms grupoAlcada,
			RapServidores rapServidores) {
		if (DominioSituacao.A.equals(grupoAlcada.getSituacao())) {
			grupoAlcada.setSituacao(DominioSituacao.I);
		} else {
			grupoAlcada.setSituacao(DominioSituacao.A);
		}
		this.getMbcGrupoAlcadaDAO().atualizar(grupoAlcada);
	}

	public MbcGrupoAlcadaAvalOpms buscaGrupoAlcadaPorSequencial(Short codigoGrupo) {
		MbcGrupoAlcadaAvalOpms item = mbcGrupoAlcadaAvalOpmsDAO.obterPorChavePrimaria(codigoGrupo,true, MbcGrupoAlcadaAvalOpms.Fields.AGH_ESPECIALIDADES);
		
		
		item.setAghEspecialidades(aghEspecialidadesDAO.obterPorChavePrimaria(item.getAghEspecialidades().getSeq()));
		item.getAghEspecialidades().getNomeEspecialidade();
		item.getAghEspecialidades().getSigla();
		return item;
	}

	public MbcGrupoAlcadaAvalOpms buscarGrupoAlcadaAtivo(
			DominioTipoConvenioOpms tipoConvenio,
			AghEspecialidades aghEspecialidades) {
		return this.getMbcGrupoAlcadaDAO().buscaGrupoAlcadaAtivo(tipoConvenio,
				aghEspecialidades);
	}
}