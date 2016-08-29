package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExamesMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelIntervaloColetaDAO;
import br.gov.mec.aghu.exames.dao.AelIntervaloColetaJnDAO;
import br.gov.mec.aghu.exames.dao.AelRefCodeDAO;
import br.gov.mec.aghu.exames.dao.AelTmpIntervaloColetaDAO;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelIntervaloColeta;
import br.gov.mec.aghu.model.AelIntervaloColetaJn;
import br.gov.mec.aghu.model.AelTmpIntervaloColeta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ManterIntervaloColetaRN extends BaseBusiness {

	@EJB
	private TempoIntervaloColetaRN tempoIntervaloColetaRN;
	
	private static final Log LOG = LogFactory.getLog(ManterIntervaloColetaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelTmpIntervaloColetaDAO aelTmpIntervaloColetaDAO;
	
	@Inject
	private AelExamesMaterialAnaliseDAO aelExamesMaterialAnaliseDAO;
	
	@Inject
	private AelIntervaloColetaDAO aelIntervaloColetaDAO;
	
	@Inject
	private AelIntervaloColetaJnDAO aelIntervaloColetaJnDAO;
	
	@Inject
	private AelRefCodeDAO aelRefCodeDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7447495778575521665L;
	protected static final String UNIDADE_MEDIDA_VOLUME_DOMINIO = "UNID MED INGERIDA";
	protected static final String TIPO_SUBSTANCIA_DOMINIO = "TIPO SUBSTANCIA";

	public enum ManterIntervaloColetaRNExceptionCode implements BusinessExceptionCode {
		AEL_00431,
		AEL_00226,
		AEL_00228,
		;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}

	protected void criarJournal(AelIntervaloColeta intervalo, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelIntervaloColetaJn intervaloJournal = BaseJournalFactory.getBaseJournal(operacao, AelIntervaloColetaJn.class, servidorLogado.getUsuario());

		intervaloJournal.setSeq(intervalo.getSeq());
		intervaloJournal.setEmaExaSigla(intervalo.getEmaExaSigla());
		intervaloJournal.setEmaManSeq(intervalo.getEmaManSeq());
		intervaloJournal.setDescricao(intervalo.getDescricao());
		intervaloJournal.setNroColetas(intervalo.getNroColetas());
		intervaloJournal.setTempo(intervalo.getTempo());
		intervaloJournal.setUnidMedidaTempo(intervalo.getUnidMedidaTempo());
		intervaloJournal.setIndSituacao(intervalo.getIndSituacao());
		intervaloJournal.setVolumeIngerido(intervalo.getVolumeIngerido());
		intervaloJournal.setUnidMedidaVolume(intervalo.getUnidMedidaVolume());
		intervaloJournal.setTipoSubstancia(intervalo.getTipoSubstancia());

		getAelIntervaloColetaJnDAO().persistir(intervaloJournal);
	}

	//--------------------------------------------------
	//Insert

	public AelIntervaloColeta inserir(AelIntervaloColeta intervalo) throws ApplicationBusinessException {
		

		//Regras pré-insert intervalo
		preInsert(intervalo);

		//Insert intervalo
		getAelIntervaloColetaDAO().persistir(intervalo);

		//Inserts tempos
		for(AelTmpIntervaloColeta tempo : intervalo.getTemposColeta()) {
			tempo.getId().setIcoSeq(intervalo.getSeq());
			tempo.setIntervaloColeta(intervalo);
			getTempoIntervaloColetaRN().inserir(tempo);
		}

		return intervalo;
	}

	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB Trigger AELT_ICO_BRI
	 * 
	 */
	protected void preInsert(AelIntervaloColeta intervalo) throws ApplicationBusinessException {
		//Verifica se o exame material análise está com indicador de uso do intervalo cadastrado ativo
		AelExamesMaterialAnalise examesMaterialAnalise = getAelExamesMaterialAnaliseDAO().buscarAelExamesMaterialAnalisePorId(intervalo.getEmaExaSigla(), intervalo.getEmaManSeq());
		if(!examesMaterialAnalise.getIndUsaIntervaloCadastrado()) {
			ManterIntervaloColetaRNExceptionCode.AEL_00431.throwException();
		}

		//Se a unidade de medida do volume não for nula, verifica se é um valor válido
		if(intervalo.getUnidMedidaVolume() != null) {
			if(getAelRefCodeDAO().verificarValorValido(intervalo.getUnidMedidaVolume(), UNIDADE_MEDIDA_VOLUME_DOMINIO) == 0) {
				ManterIntervaloColetaRNExceptionCode.AEL_00226.throwException();
			}
		}

		//Se o tipo de substância não for nulo, verifica se é um valor válido
		if(intervalo.getTipoSubstancia() != null) {
			if(getAelRefCodeDAO().verificarValorValido(intervalo.getTipoSubstancia(), TIPO_SUBSTANCIA_DOMINIO) == 0) {
				ManterIntervaloColetaRNExceptionCode.AEL_00228.throwException();
			}
		}
	}

	//--------------------------------------------------
	//Update

	public AelIntervaloColeta atualizar(AelIntervaloColeta intervalo) throws ApplicationBusinessException {
		//Recupera o objeto antigo
		AelIntervaloColeta intervaloOld = getAelIntervaloColetaDAO().obterOriginal(intervalo.getSeq());

		AelIntervaloColeta intervaloSessao = getAelIntervaloColetaDAO().obterPeloId(intervalo.getSeq());

		//Regras pré-update intervalo
		preUpdate(intervaloOld, intervalo);

		//Atualização na lista de tempos
		List<AelTmpIntervaloColeta> temposBD = this.getAelTmpIntervaloColetaDAO().listarPorIntervaloColeta(intervalo.getSeq());

		List<AelTmpIntervaloColeta> novos = new ArrayList<AelTmpIntervaloColeta>();
		for(AelTmpIntervaloColeta tempo : intervalo.getTemposColeta()) {
			boolean novo = true;
			for(AelTmpIntervaloColeta tempoBD : temposBD) {
				if(tempo.getId().getSeqp() == tempoBD.getId().getSeqp()) {
					novo = false;
					break;
				}
			}
			if(novo){
				novos.add(tempo);
			}
		}

		List<AelTmpIntervaloColeta> removidos = new ArrayList<AelTmpIntervaloColeta>();
		List<AelTmpIntervaloColeta> atualizados = new ArrayList<AelTmpIntervaloColeta>();
		for(AelTmpIntervaloColeta tempoBD : temposBD) {
			boolean removido = true;
			for(AelTmpIntervaloColeta tempo : intervalo.getTemposColeta()) {
				if(tempo.getId().getSeqp() == tempoBD.getId().getSeqp()) {
					removido = false;
					atualizados.add(new AelTmpIntervaloColeta(tempo.getId(), tempo.getTempo(), tempo.getCriadoEm(), tempo.getServidor()));
					break;
				}
			}
			if(removido){
				removidos.add(tempoBD);
			}
		}

		for(AelTmpIntervaloColeta tempo : novos) {
			//Inserts tempos
			tempo.setIntervaloColeta(intervaloSessao);
			getTempoIntervaloColetaRN().inserir(tempo);
		}
		for(AelTmpIntervaloColeta tempo : removidos) {
			//Deletes tempos
			getTempoIntervaloColetaRN().remover(tempo.getId().getIcoSeq(), tempo.getId().getSeqp());
		}
		for(AelTmpIntervaloColeta tempo : atualizados) {
			//Updates tempos
			AelTmpIntervaloColeta tempoSessao = getAelTmpIntervaloColetaDAO().obterPeloId(tempo.getId().getIcoSeq(), tempo.getId().getSeqp());
			tempoSessao.setTempo(tempo.getTempo());
			getTempoIntervaloColetaRN().atualizar(tempoSessao);
		}

		//Atualiza o objeto da sessão		
		intervaloSessao.setDescricao(intervalo.getDescricao());
		intervaloSessao.setUnidMedidaTempo(intervalo.getUnidMedidaTempo());
		intervaloSessao.setVolumeIngerido(intervalo.getVolumeIngerido());
		intervaloSessao.setUnidMedidaVolume(intervalo.getUnidMedidaVolume());
		intervaloSessao.setTipoSubstancia(intervalo.getTipoSubstancia());
		intervaloSessao.setIndSituacao(intervalo.getIndSituacao());

		intervaloSessao.setTemposColeta(novos);
		intervaloSessao.getTemposColeta().addAll(atualizados);

		//Regras pós-update intervalo
		posUpdate(intervaloOld, intervalo);

		return intervalo;
	}

	/**
	 * @ORADB Trigger AELT_ICO_BRU
	 * 
	 */
	protected void preUpdate(AelIntervaloColeta intervaloOld, AelIntervaloColeta intervaloNew) throws ApplicationBusinessException {
		//Se a unidade de medida do volume foi alterada, verifica se é um valor válido
		if(CoreUtil.modificados(intervaloNew.getUnidMedidaVolume(), intervaloOld.getUnidMedidaVolume())) {
			if(intervaloNew.getUnidMedidaVolume() != null) {
				if(getAelRefCodeDAO().verificarValorValido(intervaloNew.getUnidMedidaVolume(), UNIDADE_MEDIDA_VOLUME_DOMINIO) == 0) {
					ManterIntervaloColetaRNExceptionCode.AEL_00226.throwException();
				}
			}
		}

		//Se o tipo de substância foi alterado, verifica se é um valor válido
		if(CoreUtil.modificados(intervaloNew.getTipoSubstancia(), intervaloOld.getTipoSubstancia())) {
			if(intervaloNew.getTipoSubstancia() != null) {
				if(getAelRefCodeDAO().verificarValorValido(intervaloNew.getTipoSubstancia(), TIPO_SUBSTANCIA_DOMINIO) == 0) {
					ManterIntervaloColetaRNExceptionCode.AEL_00228.throwException();
				}
			}
		}
	}

	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB Trigger AELT_ICO_ARU
	 * 
	 */
	protected void posUpdate(AelIntervaloColeta intervaloOld, AelIntervaloColeta intervaloNew) throws ApplicationBusinessException {
		//Verifica se algum campo foi alterado
		if(CoreUtil.modificados(intervaloNew.getSeq(), intervaloOld.getSeq())
				|| CoreUtil.modificados(intervaloNew.getEmaExaSigla(), intervaloOld.getEmaExaSigla())
				|| CoreUtil.modificados(intervaloNew.getEmaManSeq(), intervaloOld.getEmaManSeq())
				|| CoreUtil.modificados(intervaloNew.getDescricao(), intervaloOld.getDescricao())
				|| CoreUtil.modificados(intervaloNew.getNroColetas(), intervaloOld.getNroColetas())
				|| CoreUtil.modificados(intervaloNew.getTempo(), intervaloOld.getTempo())
				|| CoreUtil.modificados(intervaloNew.getUnidMedidaTempo(), intervaloOld.getUnidMedidaTempo())
				|| CoreUtil.modificados(intervaloNew.getIndSituacao(), intervaloOld.getIndSituacao())
				|| CoreUtil.modificados(intervaloNew.getVolumeIngerido(), intervaloOld.getVolumeIngerido())
				|| CoreUtil.modificados(intervaloNew.getUnidMedidaVolume(), intervaloOld.getUnidMedidaVolume())
				|| CoreUtil.modificados(intervaloNew.getTipoSubstancia(), intervaloOld.getTipoSubstancia())) {

			//Cria uma entrada na journal
			criarJournal(intervaloOld, DominioOperacoesJournal.UPD);
		}
	}

	//--------------------------------------------------
	//Delete

	public void remover(Short codigo) throws ApplicationBusinessException {
		AelIntervaloColeta intervalo = getAelIntervaloColetaDAO().obterPeloId(codigo);

		if(intervalo != null) {
			//Deletes tempos
			for(AelTmpIntervaloColeta tempo : intervalo.getTemposColeta()) {
				getTempoIntervaloColetaRN().remover(tempo.getId().getIcoSeq(), tempo.getId().getSeqp());
			}

			//Delete
			getAelIntervaloColetaDAO().remover(intervalo);
		}
	}

	//--------------------------------------------------
	//Getters

	protected AelIntervaloColetaDAO getAelIntervaloColetaDAO() {
		return aelIntervaloColetaDAO;
	}

	protected AelExamesMaterialAnaliseDAO getAelExamesMaterialAnaliseDAO() {
		return aelExamesMaterialAnaliseDAO;
	}

	protected AelRefCodeDAO getAelRefCodeDAO() {
		return aelRefCodeDAO;
	}

	protected AelIntervaloColetaJnDAO getAelIntervaloColetaJnDAO() {
		return aelIntervaloColetaJnDAO;
	}

	protected AelTmpIntervaloColetaDAO getAelTmpIntervaloColetaDAO() {
		return aelTmpIntervaloColetaDAO;
	}

	protected TempoIntervaloColetaRN getTempoIntervaloColetaRN() {
		return tempoIntervaloColetaRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
