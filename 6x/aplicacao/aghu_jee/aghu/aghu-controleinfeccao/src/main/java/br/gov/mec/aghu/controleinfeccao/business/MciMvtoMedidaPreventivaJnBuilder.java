package br.gov.mec.aghu.controleinfeccao.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MciMvtoMedidaPreventivaJn;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciMvtoMedidaPreventivaJnBuilder extends BaseBusiness {

	private static final long serialVersionUID = -572837225364715078L;
	
	private static final Log LOG = LogFactory.getLog(MciMvtoMedidaPreventivaJnBuilder.class);
		
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private BaseJournalFactory baseJournalFactory;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public MciMvtoMedidaPreventivaJn construir(MciMvtoMedidaPreventivas entity, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		final MciMvtoMedidaPreventivaJn jn = baseJournalFactory.getBaseJournal(operacao, MciMvtoMedidaPreventivaJn.class, servidorLogado.getUsuario());
		
		jn.setSeq(entity.getSeq());
		jn.setPacCodigo(entity.getPaciente().getCodigo());
		jn.setAtdSeq(entity.getSeq());
		jn.setUnfSeq(obterUnfSeqJn(entity));
		jn.setUnfSeqNotificado(obterUnfSeqNotificadoJn(entity));
		jn.setQrtNumero(obterQrtNumeroJn(entity));
		jn.setQrtNumeroNotificado(obterQrtNumeroNotificadoJn(entity));
		jn.setLtoLtoId(obterLeitoJn(entity) );
		jn.setLtoLtoIdNotificado(obterLeitoNotificadoJn(entity));
		jn.setDtInicio(entity.getDataInicio());
		jn.setDtFim(entity.getDataFim());
		jn.setMotivoEncerramento(entity.getMotivoEncerramento());
		jn.setEinTipo(obterEinTipoJn(entity));
		jn.setPaiSeq(obtertPatologiaInfeccao(entity));
		jn.setMitSeq(obterMvtoInfeccaoTopografia(entity));
		jn.setIndConfirmacaoCci(entity.getConfirmacaoCci());
		jn.setIhoSeq(obterInstituicaoHospitalarJn(entity));
		jn.setIndImpressao(entity.getImpressao());
		jn.setCifSeq(entity.getCifSeq());
		jn.setIndGmr(entity.getGmr());
		jn.setIndIsolamento(entity.getIsolamento());
		jn.setCriadoEm(entity.getCriadoEm());
		
		if(entity.getServidor() != null ){
			jn.setSerMatricula(entity.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(entity.getServidor().getId().getVinCodigo());
		}
	
		if(entity.getServidorConfirmado() != null ){
			jn.setSerMatriculaConfirmado(entity.getServidorConfirmado().getId().getMatricula());
			jn.setSerVinCodigoConfirmado(entity.getServidorConfirmado().getId().getVinCodigo());
		}

		if(entity.getServidorEncerrado() != null ) {
			jn.setSerMatriculaEncerrado(entity.getServidorEncerrado().getId().getMatricula());
			jn.setSerVinCodigoEncerrado(entity.getServidorEncerrado().getId().getVinCodigo());
		}
		
		return jn;
	}

	private Integer obterMvtoInfeccaoTopografia(MciMvtoMedidaPreventivas entity) {
		return entity.getMciMvtoInfeccaoTopografias() != null ? entity.getMciMvtoInfeccaoTopografias().getSeq() : null;
	}
	
	private Integer obtertPatologiaInfeccao(MciMvtoMedidaPreventivas entity) {
		return entity.getPatologiaInfeccao() != null ? entity.getPatologiaInfeccao().getSeq() : null;
	}

	private String obterEinTipoJn(MciMvtoMedidaPreventivas entity) {
		return entity.getMciEtiologiaInfeccao() != null ? entity.getMciEtiologiaInfeccao().getCodigo() : null;
	}

	private Short obterUnfSeqJn(MciMvtoMedidaPreventivas entity) {
		return entity.getUnidadeFuncional().getSeq();
	}

	private Short obterUnfSeqNotificadoJn(MciMvtoMedidaPreventivas entity) {
		return entity.getUnidadeFuncionalNotificada().getSeq();
	}

	private Short obterQrtNumeroJn(MciMvtoMedidaPreventivas entity) {
		return entity.getQuarto() != null ? entity.getQuarto().getNumero(): null;
	}

	private Short obterQrtNumeroNotificadoJn(MciMvtoMedidaPreventivas entity) {
		return entity.getQuartoNotificado() != null ? entity.getQuartoNotificado().getNumero() : null;
	}

	private String obterLeitoJn(MciMvtoMedidaPreventivas entity) {
		return entity.getLeito() != null ? entity.getLeito().getLeitoID() : null;
	}

	private String obterLeitoNotificadoJn(MciMvtoMedidaPreventivas entity) {
		return entity.getLeitoNotificado() != null ? entity.getLeitoNotificado().getLeitoID() : null;
	}

	private Integer obterInstituicaoHospitalarJn(MciMvtoMedidaPreventivas entity) {
		return entity.getIhoSeq() != null ? entity.getIhoSeq().getSeq() : null;
	}

}
