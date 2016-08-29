package br.gov.mec.aghu.emergencia.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.ambulatorio.dao.MamItemMedicacaoDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgGravidadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgMedicacaoDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgMedicacaoJnDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamObrigatoriedade;
import br.gov.mec.aghu.model.MamTrgGravidade;
import br.gov.mec.aghu.model.MamTrgMedicacaoJn;
import br.gov.mec.aghu.model.MamTrgMedicacoes;
import br.gov.mec.aghu.model.MamTrgMedicacoesId;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MamTrgMedicacaoRN extends BaseBusiness {

	private static final long serialVersionUID = 761590482783879708L;

	@Inject
	private MamTrgMedicacaoDAO mamTrgMedicacaoDAO;
	
	@Inject
	private MamItemMedicacaoDAO mamItemMedicacaoDAO;
	
	@Inject
	private MamTrgMedicacaoJnDAO mamTrgMedicacaoJnDAO;
	
	@Inject
	private MamTrgGravidadeDAO mamTrgGravidadeDAO;
	
	@Inject
	private RapServidoresDAO servidorDAO;

	@Inject @QualificadorUsuario
	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum MamTrgMedicacaoRNExceptionCode implements BusinessExceptionCode {
		MAM_02742_1, ERRO_EXCLUIR_MEDICACAO_OBRIGATORIO
	}
	
	public MamTrgMedicacoes inserirTrgMedicacao(Long trgSeq, Integer mdmSeq, String nomeComputador) {
		MamTrgMedicacoes mamTrgMedicacoes = new MamTrgMedicacoes();
		MamTrgMedicacoesId id = new MamTrgMedicacoesId(trgSeq, this.mamTrgMedicacaoDAO.obterMaxSeqPTrgMedicacao(trgSeq));
		mamTrgMedicacoes.setId(id);
		mamTrgMedicacoes.setItemMedicacao(this.mamItemMedicacaoDAO.obterPorChavePrimaria(mdmSeq));
		mamTrgMedicacoes.setComplemento(null);
		mamTrgMedicacoes.setDthrInformada(new Date());
		mamTrgMedicacoes.setCriadoEm(new Date());
		mamTrgMedicacoes.setMicNome(nomeComputador);		
		mamTrgMedicacoes.setServidor(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		mamTrgMedicacoes.setUso(Boolean.FALSE);
		mamTrgMedicacoes.setConsistenciaOk(Boolean.FALSE);
		
		this.mamTrgMedicacaoDAO.persistir(mamTrgMedicacoes);
		
		return mamTrgMedicacoes;
	}
	
	public void excluirTrgMedicacao(MamTrgMedicacoes mamTrgMedicacoes) throws ApplicationBusinessException {
		
		MamTrgMedicacoes mamTrgMedicacoeExcluir = this.mamTrgMedicacaoDAO.obterPorChavePrimaria(mamTrgMedicacoes.getId());
		
		List<MamTrgMedicacoes> listTrgMedicacoes = this.mamTrgMedicacaoDAO.listarMamTrgMedicacoesPorTriagemEItemMedicacao(mamTrgMedicacoeExcluir.getId().getTrgSeq(),
				mamTrgMedicacoeExcluir.getItemMedicacao().getSeq());
		if(listTrgMedicacoes.size() == 1) {
			MamTrgGravidade gravidade = this.mamTrgGravidadeDAO.obterUltimaGravidadePorTriagem(mamTrgMedicacoeExcluir.getId().getTrgSeq());
			if(gravidade != null && gravidade.getMamDescritor() != null &&
					gravidade.getMamDescritor().getMamObrigatoriedades() != null
					&& !gravidade.getMamDescritor().getMamObrigatoriedades().isEmpty()) {
				for(MamObrigatoriedade obgt : gravidade.getMamDescritor().getMamObrigatoriedades()) {
					if(obgt.getMamItemMedicacao() != null && 
							obgt.getMamItemMedicacao().getSeq().equals(mamTrgMedicacoeExcluir.getItemMedicacao().getSeq())) {
						throw new ApplicationBusinessException(MamTrgMedicacaoRNExceptionCode.ERRO_EXCLUIR_MEDICACAO_OBRIGATORIO);
					}
				}
			}
		}
		
		Calendar dataMenosMeiaHora = Calendar.getInstance();
		dataMenosMeiaHora.add(Calendar.MINUTE, -30);
		
		if (mamTrgMedicacoeExcluir.getConsistenciaOk().equals(Boolean.TRUE)
				&& DateUtil.validaDataMaior(mamTrgMedicacoeExcluir.getDthrConsistenciaOk(), dataMenosMeiaHora.getTime())) {
			throw new ApplicationBusinessException(MamTrgMedicacaoRNExceptionCode.MAM_02742_1);
		}
		MamTrgMedicacoes mamTrgMedicacoesOriginal = this.mamTrgMedicacaoDAO.obterOriginal(mamTrgMedicacoes.getId());
		
		this.inserirJournalTrgMedicacao(mamTrgMedicacoesOriginal, DominioOperacoesJournal.DEL);
		
		this.mamTrgMedicacaoDAO.remover(mamTrgMedicacoeExcluir);
	}
	
	public void inserirJournalTrgMedicacao(MamTrgMedicacoes mamTrgMedicacoesOriginal, DominioOperacoesJournal operacao) {

		MamTrgMedicacaoJn mamTrgMedicacaoJn = new MamTrgMedicacaoJn();

		mamTrgMedicacaoJn.setNomeUsuario(usuario.getLogin());
		mamTrgMedicacaoJn.setOperacao(operacao);
		mamTrgMedicacaoJn.setTrgSeq(mamTrgMedicacoesOriginal.getMamTriagens().getSeq());
		mamTrgMedicacaoJn.setSeqp(mamTrgMedicacoesOriginal.getId().getSeqp());
		mamTrgMedicacaoJn.setComplemento(mamTrgMedicacoesOriginal.getComplemento());
		mamTrgMedicacaoJn.setMicNome(mamTrgMedicacoesOriginal.getMicNome());
		mamTrgMedicacaoJn.setCriadoEm(mamTrgMedicacoesOriginal.getCriadoEm());
		mamTrgMedicacaoJn.setSerMatricula(mamTrgMedicacoesOriginal.getServidor().getId().getMatricula());
		mamTrgMedicacaoJn.setSerVinCodigo(mamTrgMedicacoesOriginal.getServidor().getId().getVinCodigo());
		mamTrgMedicacaoJn.setMamItemMedicacao(mamTrgMedicacoesOriginal.getItemMedicacao());
		mamTrgMedicacaoJn.setIndUso(mamTrgMedicacoesOriginal.getUso());
		mamTrgMedicacaoJn.setIndConsistenciaOk(mamTrgMedicacoesOriginal.getConsistenciaOk());
		mamTrgMedicacaoJn.setDtHrInformada(mamTrgMedicacoesOriginal.getDthrInformada());
		mamTrgMedicacaoJn.setDtHrConsistenciaOk(mamTrgMedicacoesOriginal.getDthrConsistenciaOk());

		this.mamTrgMedicacaoJnDAO.persistir(mamTrgMedicacaoJn);
	}
	
	public void atualizarTrgMedicacao(MamTrgMedicacoes mamTrgMedicacoes, MamTrgMedicacoes mamTrgMedicacoesOriginal) {
				
		mamTrgMedicacoes.setServidor(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		if (mamTrgMedicacoes.getComplemento() != null) {
			mamTrgMedicacoes.setUso(Boolean.TRUE);
		}
		
		this.inserirJournalTrgMedicacao(mamTrgMedicacoesOriginal, DominioOperacoesJournal.UPD);
		
		this.mamTrgMedicacaoDAO.atualizar(mamTrgMedicacoes);
	}
}
