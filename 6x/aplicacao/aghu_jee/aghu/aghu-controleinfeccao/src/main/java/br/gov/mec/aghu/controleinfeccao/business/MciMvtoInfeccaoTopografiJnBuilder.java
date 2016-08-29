package br.gov.mec.aghu.controleinfeccao.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MciMvtoInfeccaoTopografiJn;
import br.gov.mec.aghu.model.MciMvtoInfeccaoTopografias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciMvtoInfeccaoTopografiJnBuilder extends BaseBusiness {

	private static final long serialVersionUID = -572837225364715078L;
	
	private static final Log LOG = LogFactory.getLog(MciMvtoInfeccaoTopografiJnBuilder.class);
		
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private BaseJournalFactory baseJournalFactory;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	
	public MciMvtoInfeccaoTopografiJn construir(MciMvtoInfeccaoTopografias entity, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		final MciMvtoInfeccaoTopografiJn jn = baseJournalFactory.getBaseJournal(operacao, MciMvtoInfeccaoTopografiJn.class, servidorLogado.getUsuario());
		
		jn.setSeq(entity.getSeq());
		jn.setAtdSeq(entity.getSeq());
		jn.setPacCodigo(entity.getPaciente().getCodigo());
		jn.setEinTipo(obterEinTipoJn(entity));
		jn.setTopSeq(obterTopSeqJn(entity));
		jn.setCriadoEm(entity.getCriadoEm());
		jn.setDtInicio(entity.getDataInicio());
		jn.setIndConfirmacaoCci(entity.getConfirmacaoCci());
		
		if(entity.getServidor() != null ){
			jn.setSerMatricula(entity.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(entity.getServidor().getId().getVinCodigo());
		}
		
		jn.setUnfSeq(obterUnfSeqJn(entity));
		jn.setUnfSeqNotificado(obterUnfSeqNotificadoJn(entity));
		jn.setQrtNumero(obterQrtNumeroJn(entity));
		jn.setQrtNumeroNotificado(obterQrtNumeroNotificadoJn(entity));
		jn.setLtoLtoId(obterLeitoJn(entity) );
		jn.setLtoLtoIdNotificado(obterLeitoNotificadoJn(entity));
		
		if(entity.getServidorConfirmado() != null ){
			jn.setSerMatriculaConfirmado(entity.getServidorConfirmado().getId().getMatricula());
			jn.setSerVinCodigoConfirmado(entity.getServidorConfirmado().getId().getVinCodigo());
		}
		
		jn.setDtFim(entity.getDataFim());
		jn.setMotivoEncerramento(entity.getMotivoEncerramento());
		
		if(entity.getServidorEncerrado() != null ) {
			jn.setSerMatriculaEncerrado(entity.getServidorEncerrado().getId().getMatricula());
			jn.setSerVinCodigoEncerrado(entity.getServidorEncerrado().getId().getVinCodigo());
		}
		
		jn.setIhoSeq(obterInstituicaoHospitalarJn(entity));
		jn.setRniPnnSeq(entity.getRniPnnSeq());
		jn.setRniSeqp(entity.getRniSeqp());
		
		return jn;
	}
	
	private String obterEinTipoJn(MciMvtoInfeccaoTopografias entity) {
		return entity.getEtiologiaInfeccao() != null ? entity.getEtiologiaInfeccao().getCodigo() : null;
	}

	private Short obterTopSeqJn(MciMvtoInfeccaoTopografias entity) {
		return entity.getTopografiaProcedimento() != null ?  entity.getTopografiaProcedimento().getSeq() : null;
	}

	private Short obterUnfSeqJn(MciMvtoInfeccaoTopografias entity) {
		return entity.getUnidadeFuncional().getSeq();
	}

	private Short obterUnfSeqNotificadoJn(MciMvtoInfeccaoTopografias entity) {
		return entity.getUnidadeFuncionalNotificada().getSeq();
	}

	private Short obterQrtNumeroJn(MciMvtoInfeccaoTopografias entity) {
		return entity.getQuarto() != null ? entity.getQuarto().getNumero(): null;
	}

	private Short obterQrtNumeroNotificadoJn(MciMvtoInfeccaoTopografias entity) {
		return entity.getQuartoNotificado() != null ? entity.getQuartoNotificado().getNumero() : null;
	}

	private String obterLeitoJn(MciMvtoInfeccaoTopografias entity) {
		return entity.getLeito() != null ? entity.getLeito().getLeitoID() : null;
	}

	private String obterLeitoNotificadoJn(MciMvtoInfeccaoTopografias entity) {
		return entity.getLeitoNotificado() != null ? entity.getLeitoNotificado().getLeitoID() : null;
	}

	private Integer obterInstituicaoHospitalarJn(MciMvtoInfeccaoTopografias entity) {
		return entity.getInstituicaoHospitalar() != null ? entity.getInstituicaoHospitalar().getSeq() : null;
	}

}
