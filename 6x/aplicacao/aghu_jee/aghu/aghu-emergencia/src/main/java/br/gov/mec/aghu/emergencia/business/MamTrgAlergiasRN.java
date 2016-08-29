package br.gov.mec.aghu.emergencia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.ambulatorio.dao.MamTrgAlergiasDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgAlergiaJnDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.emergencia.vo.MamTriagemVO;
import br.gov.mec.aghu.model.MamTrgAlergiaJn;
import br.gov.mec.aghu.model.MamTrgAlergias;
import br.gov.mec.aghu.model.MamTrgAlergiasId;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MamTrgAlergiasRN extends BaseBusiness {

	private static final long serialVersionUID = 761590482783879708L;

	@Inject
	private MamTrgAlergiasDAO mamTrgAlergiasDAO;

	@Inject
	private MamTrgAlergiaJnDAO mamTrgAlergiaJnDAO;

	@Inject @QualificadorUsuario
	private Usuario usuario;


	@Override
	protected Log getLogger() {
		return null;
	}
	
	public void atualizarAlergia(MamTrgAlergias alergia, MamTrgAlergias mamTrgAlergiasOriginal, MamTriagemVO vo)
			throws ApplicationBusinessException {
		
		alergia.setDescricao(vo.getAlergias());
		
		if (isMamTrgAlergiasAlterada(mamTrgAlergiasOriginal, alergia)) {
			
			this.inserirJournalAlergias(mamTrgAlergiasOriginal, DominioOperacoesJournal.UPD);
		}
		this.mamTrgAlergiasDAO.atualizar(alergia);
	}
	
	private boolean isMamTrgAlergiasAlterada(MamTrgAlergias mamTrgAlergiasOriginal, MamTrgAlergias alergia) {
		return CoreUtil.modificados(alergia.getDescricao(), mamTrgAlergiasOriginal.getDescricao()) ||	
				CoreUtil.modificados(alergia.getPacCodigo(), mamTrgAlergiasOriginal.getPacCodigo()) ||	
				CoreUtil.modificados(alergia.getMicNome(), mamTrgAlergiasOriginal.getMicNome()) ||
				CoreUtil.modificados(alergia.getCriadoEm(), mamTrgAlergiasOriginal.getCriadoEm()) ||
				CoreUtil.modificados(alergia.getSerVinCodigo(), mamTrgAlergiasOriginal.getSerVinCodigo()) ||
				CoreUtil.modificados(alergia.getSerMatricula(), mamTrgAlergiasOriginal.getSerMatricula());
	}

	/***
	 * @ORADB MAM_TRG_ALERGIAS.MAMT_TAI_ARU - Executar depois de update na tabela MAM_TRG_ALERGIAS
	 * @param mamTriagensOriginal
	 * @param operacao
	 */	
	public void inserirJournalAlergias(MamTrgAlergias mamTrgAlergiasOriginal, DominioOperacoesJournal operacao) {

		MamTrgAlergiaJn mamTrgAlergiaJn = new MamTrgAlergiaJn();

		mamTrgAlergiaJn.setNomeUsuario(usuario.getLogin());
		mamTrgAlergiaJn.setOperacao(operacao);
		mamTrgAlergiaJn.setPacCodigo(mamTrgAlergiasOriginal.getPacCodigo());
		mamTrgAlergiaJn.setMicNome(mamTrgAlergiasOriginal.getMicNome());
		mamTrgAlergiaJn.setCriadoEm(mamTrgAlergiasOriginal.getCriadoEm());
		mamTrgAlergiaJn.setSerVinCodigo(mamTrgAlergiasOriginal.getSerVinCodigo());
		mamTrgAlergiaJn.setSerMatricula(mamTrgAlergiasOriginal.getSerMatricula());
		mamTrgAlergiaJn.setTrgSeq(mamTrgAlergiasOriginal.getId().getTrgSeq());
		mamTrgAlergiaJn.setSeqp(mamTrgAlergiasOriginal.getId().getSeqp());
		mamTrgAlergiaJn.setDescricao(mamTrgAlergiasOriginal.getDescricao());
		
		this.mamTrgAlergiaJnDAO.persistir(mamTrgAlergiaJn);
	}

	public MamTrgAlergias inserirTrgAlergias(MamTriagemVO vo, String hostName) {
		MamTrgAlergias alergia = preInserirMamTrgAlergias(hostName);
		MamTrgAlergiasId id = new MamTrgAlergiasId(vo.getTrgSeq(), this.mamTrgAlergiasDAO.obterMaxSeqPTrgAlergias(vo.getTrgSeq()));
		alergia.setId(id);
		alergia.setDescricao(vo.getAlergias());
		alergia.setPacCodigo(vo.getPacCodigo());
		alergia.setMicNome(hostName);
		
		this.mamTrgAlergiasDAO.persistir(alergia);
		this.mamTrgAlergiasDAO.flush();
		
		return alergia;
	}

	/**
	 * @ORADB MAM_TRG_ALERGIAS.MAMT_TAI_BRI - Executar antes de inserir na tabela MAM_TRG_ALERGIAS
	 * @param hostName
	 * @return alergia
	 */
	public MamTrgAlergias preInserirMamTrgAlergias(String hostName) {
		MamTrgAlergias alergia = new MamTrgAlergias();
		alergia.setCriadoEm(new Date());
		alergia.setSerMatricula(usuario.getMatricula());
		alergia.setSerVinCodigo(usuario.getVinculo());
		alergia.setMicNome(hostName);
		
		return alergia;
	}
}
