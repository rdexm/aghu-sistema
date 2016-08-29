package br.gov.mec.aghu.emergencia.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.dao.MamItemGeralDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgGeralDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgGeralJnDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgGravidadeDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamObrigatoriedade;
import br.gov.mec.aghu.model.MamTrgGerais;
import br.gov.mec.aghu.model.MamTrgGeralId;
import br.gov.mec.aghu.model.MamTrgGeralJn;
import br.gov.mec.aghu.model.MamTrgGravidade;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MamTrgGeralRN extends BaseBusiness {

	private static final long serialVersionUID = 761590482783879708L;

	@Inject
	private MamTrgGeralDAO mamTrgGeralDAO;
	
	@Inject
	private MamItemGeralDAO mamItemGeralDAO;
	
	@Inject
	private MamTrgGravidadeDAO mamTrgGravidadeDAO;
	
	@Inject
	private MamTrgGeralJnDAO mamTrgGeralJnDAO;
	
	@Inject @QualificadorUsuario
	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum MamTrgGeralRNExceptionCode implements BusinessExceptionCode {
		MAM_02742_1, ERRO_EXCLUIR_ITEM_GERAL_OBRIGATORIO
	}
	
	public MamTrgGerais inserirTrgGeral(Long trgSeq, Integer itgSeq, String nomeComputador) {
		MamTrgGerais mamTrgGeral = new MamTrgGerais();
		MamTrgGeralId id = new MamTrgGeralId(trgSeq, this.mamTrgGeralDAO.obterMaxSeqPTrgGeral(trgSeq));
		mamTrgGeral.setId(id);
		mamTrgGeral.setMamItemGeral(this.mamItemGeralDAO.obterPorChavePrimaria(itgSeq));
		mamTrgGeral.setComplemento(null);
		mamTrgGeral.setDtHrInformada(new Date());
		mamTrgGeral.setCriadoEm(new Date());
		mamTrgGeral.setMicNome(nomeComputador);
		mamTrgGeral.setSerMatricula(usuario.getMatricula());
		mamTrgGeral.setSerVinCodigo(usuario.getVinculo());
		mamTrgGeral.setIndUso(Boolean.FALSE);
		mamTrgGeral.setIndConsistenciaOk(Boolean.FALSE);
		
		this.mamTrgGeralDAO.persistir(mamTrgGeral);
		
		return mamTrgGeral;
	}
	
	public void excluirTrgGeral(MamTrgGerais mamTrgGerais) throws ApplicationBusinessException {
		
		MamTrgGerais mamTrgGeralExcluir = this.mamTrgGeralDAO.obterPorChavePrimaria(mamTrgGerais.getId());
		
		List<MamTrgGerais> listTrgGerais = this.mamTrgGeralDAO.listarMamTrgGeralPorItemGeralETriagem(mamTrgGeralExcluir.getId().getTrgSeq(),
				mamTrgGeralExcluir.getMamItemGeral().getSeq());
		if(listTrgGerais.size() == 1) {
			MamTrgGravidade gravidade = this.mamTrgGravidadeDAO.obterUltimaGravidadePorTriagem(mamTrgGeralExcluir.getId().getTrgSeq());
			if(gravidade != null && gravidade.getMamDescritor() != null &&
					gravidade.getMamDescritor().getMamObrigatoriedades() != null
					&& !gravidade.getMamDescritor().getMamObrigatoriedades().isEmpty()) {
				for(MamObrigatoriedade obgt : gravidade.getMamDescritor().getMamObrigatoriedades()) {
					if(obgt.getMamItemGeral() != null
							&& obgt.getMamItemGeral().getSeq().equals(mamTrgGeralExcluir.getMamItemGeral().getSeq())) {
						throw new ApplicationBusinessException(MamTrgGeralRNExceptionCode.ERRO_EXCLUIR_ITEM_GERAL_OBRIGATORIO);
					}
				}
			}
		}
		
		Calendar dataMenosMeiaHora = Calendar.getInstance();
		dataMenosMeiaHora.add(Calendar.MINUTE, -30);
		
		if (mamTrgGeralExcluir.getIndConsistenciaOk().equals(Boolean.TRUE)
				&& DateUtil.validaDataMaior(mamTrgGeralExcluir.getDtHrConsistenciaOk(), dataMenosMeiaHora.getTime())) {
			throw new ApplicationBusinessException(MamTrgGeralRNExceptionCode.MAM_02742_1);
		}
		MamTrgGerais mamTrgGeraisOriginal = this.mamTrgGeralDAO.obterOriginal(mamTrgGerais.getId());
		
		this.inserirJournalTrgGeral(mamTrgGeraisOriginal, DominioOperacoesJournal.DEL);
		
		this.mamTrgGeralDAO.remover(mamTrgGeralExcluir);
	}
	
	public void inserirJournalTrgGeral(MamTrgGerais mamTrgGeraisOriginal, DominioOperacoesJournal operacao) {

		MamTrgGeralJn mamTrgGeralJn = new MamTrgGeralJn();

		mamTrgGeralJn.setNomeUsuario(usuario.getLogin());
		mamTrgGeralJn.setOperacao(operacao);
		mamTrgGeralJn.setTrgSeq(mamTrgGeraisOriginal.getMamTriagens().getSeq());
		mamTrgGeralJn.setSeqp(mamTrgGeraisOriginal.getId().getSeqp());
		mamTrgGeralJn.setComplemento(mamTrgGeraisOriginal.getComplemento());
		mamTrgGeralJn.setMicNome(mamTrgGeraisOriginal.getMicNome());
		mamTrgGeralJn.setCriadoEm(mamTrgGeraisOriginal.getCriadoEm());
		mamTrgGeralJn.setSerMatricula(mamTrgGeraisOriginal.getSerMatricula());
		mamTrgGeralJn.setSerVinCodigo(mamTrgGeraisOriginal.getSerVinCodigo());
		mamTrgGeralJn.setMamItemGeral(mamTrgGeraisOriginal.getMamItemGeral());
		mamTrgGeralJn.setIndUso(mamTrgGeraisOriginal.getIndUso());
		mamTrgGeralJn.setIndConsistenciaOk(mamTrgGeraisOriginal.getIndConsistenciaOk());
		mamTrgGeralJn.setDtHrInformada(mamTrgGeraisOriginal.getDtHrInformada());
		mamTrgGeralJn.setDtHrConsistenciaOk(mamTrgGeraisOriginal.getDtHrConsistenciaOk());

		this.mamTrgGeralJnDAO.persistir(mamTrgGeralJn);
	}
	
	public void atualizarTrgGeral(MamTrgGerais mamTrgGerais, MamTrgGerais mamTrgGeraisOriginal) {
		
		mamTrgGerais.setSerMatricula(usuario.getMatricula());
		mamTrgGerais.setSerVinCodigo(usuario.getVinculo());
		if (mamTrgGerais.getComplemento() != null) {
			mamTrgGerais.setIndUso(Boolean.TRUE);
		}
		
		this.inserirJournalTrgGeral(mamTrgGeraisOriginal, DominioOperacoesJournal.UPD);
		
		this.mamTrgGeralDAO.atualizar(mamTrgGerais);
	}
}
