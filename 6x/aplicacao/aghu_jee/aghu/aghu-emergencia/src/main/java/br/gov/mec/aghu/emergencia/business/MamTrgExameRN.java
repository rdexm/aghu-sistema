package br.gov.mec.aghu.emergencia.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.ambulatorio.dao.MamItemExameDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgExameDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgExameJnDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgGravidadeDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamObrigatoriedade;
import br.gov.mec.aghu.model.MamTrgExameJn;
import br.gov.mec.aghu.model.MamTrgExames;
import br.gov.mec.aghu.model.MamTrgExamesId;
import br.gov.mec.aghu.model.MamTrgGravidade;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MamTrgExameRN extends BaseBusiness {

	private static final long serialVersionUID = 761590482783879708L;

	@Inject
	private MamTrgExameDAO mamTrgExameDAO;
	
	@Inject
	private MamItemExameDAO mamItemExameDAO;
	
	@Inject
	private MamTrgExameJnDAO mamTrgExameJnDAO;
	
	@Inject
	private MamTrgGravidadeDAO mamTrgGravidadeDAO;

	@Inject @QualificadorUsuario
	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum MamTrgExameRNExceptionCode implements BusinessExceptionCode {
		MAM_02742_1, ERRO_EXCLUIR_EXAME_OBRIGATORIO
	}
	
	public MamTrgExames inserirTrgExame(Long trgSeq, Integer emsSeq, String nomeComputador) {
		MamTrgExames mamTrgExames = new MamTrgExames();
		MamTrgExamesId id = new MamTrgExamesId(trgSeq, this.mamTrgExameDAO.obterMaxSeqPTrgExame(trgSeq));
		mamTrgExames.setId(id);
		mamTrgExames.setItemExame(this.mamItemExameDAO.obterPorChavePrimaria(emsSeq));
		mamTrgExames.setComplemento(null);
		mamTrgExames.setDthrInformada(new Date());
		mamTrgExames.setCriadoEm(new Date());
		mamTrgExames.setMicNome(nomeComputador);
		mamTrgExames.setSerMatricula(usuario.getMatricula());
		mamTrgExames.setSerVinCodigo(usuario.getVinculo());
		mamTrgExames.setIndUso(Boolean.FALSE);
		mamTrgExames.setIndConsistenciaOk(Boolean.FALSE);
		
		this.mamTrgExameDAO.persistir(mamTrgExames);
		
		return mamTrgExames;
	}
	
	public void excluirTrgExame(MamTrgExames mamTrgExames) throws ApplicationBusinessException {
		
		MamTrgExames mamTrgExameExcluir = this.mamTrgExameDAO.obterPorChavePrimaria(mamTrgExames.getId());
		
		List<MamTrgExames> listTrgExames = this.mamTrgExameDAO.listarMamTrgExamesPorTriagemEItemExame(mamTrgExameExcluir.getId().getTrgSeq(),
				mamTrgExameExcluir.getItemExame().getSeq());
		if(listTrgExames.size() == 1) {
			MamTrgGravidade gravidade = this.mamTrgGravidadeDAO.obterUltimaGravidadePorTriagem(mamTrgExameExcluir.getId().getTrgSeq());
			if(gravidade != null && gravidade.getMamDescritor() != null &&
					gravidade.getMamDescritor().getMamObrigatoriedades() != null
					&& !gravidade.getMamDescritor().getMamObrigatoriedades().isEmpty()) {
				for(MamObrigatoriedade obgt : gravidade.getMamDescritor().getMamObrigatoriedades()) {
					if(obgt.getMamItemExame() != null
							&& obgt.getMamItemExame().getSeq().equals(mamTrgExameExcluir.getItemExame().getSeq())) {
						throw new ApplicationBusinessException(MamTrgExameRNExceptionCode.ERRO_EXCLUIR_EXAME_OBRIGATORIO);
					}
				}
			}
		}
		
		Calendar dataMenosMeiaHora = Calendar.getInstance();
		dataMenosMeiaHora.add(Calendar.MINUTE, -30);
		
		if (mamTrgExameExcluir.getIndConsistenciaOk().equals(Boolean.TRUE)
				&& DateUtil.validaDataMaior(mamTrgExameExcluir.getDthrConsistenciaOk(), dataMenosMeiaHora.getTime())) {
			throw new ApplicationBusinessException(MamTrgExameRNExceptionCode.MAM_02742_1);
		}
		MamTrgExames mamTrgExamesOriginal = this.mamTrgExameDAO.obterOriginal(mamTrgExames.getId());
		
		this.inserirJournalTrgExame(mamTrgExamesOriginal, DominioOperacoesJournal.DEL);
		
		this.mamTrgExameDAO.remover(mamTrgExameExcluir);
	}
	
	public void inserirJournalTrgExame(MamTrgExames mamTrgExamesOriginal, DominioOperacoesJournal operacao) {

		MamTrgExameJn mamTrgExameJn = new MamTrgExameJn();

		mamTrgExameJn.setNomeUsuario(usuario.getLogin());
		mamTrgExameJn.setOperacao(operacao);
		mamTrgExameJn.setTrgSeq(mamTrgExamesOriginal.getMamTriagens().getSeq());
		mamTrgExameJn.setSeqp(mamTrgExamesOriginal.getId().getSeqp());
		mamTrgExameJn.setComplemento(mamTrgExamesOriginal.getComplemento());
		mamTrgExameJn.setMicNome(mamTrgExamesOriginal.getMicNome());
		mamTrgExameJn.setCriadoEm(mamTrgExamesOriginal.getCriadoEm());
		mamTrgExameJn.setSerMatricula(mamTrgExamesOriginal.getSerMatricula());
		mamTrgExameJn.setSerVinCodigo(mamTrgExamesOriginal.getSerVinCodigo());
		mamTrgExameJn.setMamItemExame(mamTrgExamesOriginal.getItemExame());
		mamTrgExameJn.setIndUso(mamTrgExamesOriginal.getIndUso());
		mamTrgExameJn.setIndConsistenciaOk(mamTrgExamesOriginal.getIndConsistenciaOk());
		mamTrgExameJn.setDtHrInformada(mamTrgExamesOriginal.getDthrInformada());
		mamTrgExameJn.setDtHrConsistenciaOk(mamTrgExamesOriginal.getDthrConsistenciaOk());

		this.mamTrgExameJnDAO.persistir(mamTrgExameJn);
	}
	
	public void atualizarTrgExame(MamTrgExames mamTrgExames, MamTrgExames mamTrgExamesOriginal) {
		
		mamTrgExames.setSerMatricula(usuario.getMatricula());
		mamTrgExames.setSerVinCodigo(usuario.getVinculo());
		if (mamTrgExames.getComplemento() != null) {
			mamTrgExames.setIndUso(Boolean.TRUE);
		}
		
		this.inserirJournalTrgExame(mamTrgExamesOriginal, DominioOperacoesJournal.UPD);
		
		this.mamTrgExameDAO.atualizar(mamTrgExames);
	}
}
