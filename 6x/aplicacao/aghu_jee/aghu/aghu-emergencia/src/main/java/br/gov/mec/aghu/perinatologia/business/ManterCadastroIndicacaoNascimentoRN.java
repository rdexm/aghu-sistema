package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoIndicacaoNascimento;
import br.gov.mec.aghu.model.McoIndicacaoNascimentoJn;
import br.gov.mec.aghu.perinatologia.dao.McoIndicacaoNascimentoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIndicacaoNascimentoJnDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
/**
 * @author israel.haas
 */
@Stateless
public class ManterCadastroIndicacaoNascimentoRN extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;

	@Inject
	private McoIndicacaoNascimentoDAO mcoIndicacaoNascimentoDAO;
	
	@Inject
	private McoIndicacaoNascimentoJnDAO mcoIndicacaoNascimentoJnDAO;
	
	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	public void inserirIndicacaoNascimento(McoIndicacaoNascimento indicacaoNascimento) throws BaseException {
		this.preInserir(indicacaoNascimento);
		this.mcoIndicacaoNascimentoDAO.persistir(indicacaoNascimento);
	}
	
	/**
	 * @ORADB MCO_INDICACAO_NASCIMENTOS.MCOT_INA_BRI – trigger before insert
	 * @param indicacaoNascimento
	 */
	private void preInserir(McoIndicacaoNascimento indicacaoNascimento) {
		indicacaoNascimento.setCriadoEm(new Date());
		indicacaoNascimento.setSerMatricula(usuario.getMatricula());
		indicacaoNascimento.setSerVinCodigo(usuario.getVinculo());
	}
	
	public void atualizarIndicacaoNascimento(McoIndicacaoNascimento indicacaoNascimento,
			McoIndicacaoNascimento indicacaoNascimentoOriginal) throws BaseException {
		
		if (isIndicacaoNascimentoAlterada(indicacaoNascimento, indicacaoNascimentoOriginal)) {
			this.inserirJournalIndicacaoNascimento(indicacaoNascimentoOriginal, DominioOperacoesJournal.UPD);
		}
		
		this.mcoIndicacaoNascimentoDAO.merge(indicacaoNascimento);
	}
	
	private boolean isIndicacaoNascimentoAlterada(McoIndicacaoNascimento indicacaoNascimento,
			McoIndicacaoNascimento indicacaoNascimentoOriginal) {
		
		return CoreUtil.modificados(indicacaoNascimento.getDescricao(), indicacaoNascimentoOriginal.getDescricao()) ||
				CoreUtil.modificados(indicacaoNascimento.getTipoIndicacao(), indicacaoNascimentoOriginal.getTipoIndicacao()) ||
				CoreUtil.modificados(indicacaoNascimento.getIndSituacao(), indicacaoNascimentoOriginal.getIndSituacao());
	}
	
	/**
	 * @ORADB MCO_INDICACAO_NASCIMENTOS.MCOT_INA_ARU – trigger after update
	 * @param indicacaoNascimentoOriginal
	 * @param operacao
	 */
	public void inserirJournalIndicacaoNascimento(McoIndicacaoNascimento indicacaoNascimentoOriginal, DominioOperacoesJournal operacao) {
		
		McoIndicacaoNascimentoJn mcoIndicacaoNascimentoJn = BaseJournalFactory.getBaseJournal(operacao, McoIndicacaoNascimentoJn.class, usuario.getLogin());
		
		mcoIndicacaoNascimentoJn.setSeq(indicacaoNascimentoOriginal.getSeq());
		mcoIndicacaoNascimentoJn.setTipoIndicacao(indicacaoNascimentoOriginal.getTipoIndicacao());
		mcoIndicacaoNascimentoJn.setCriadoEm(indicacaoNascimentoOriginal.getCriadoEm());
		mcoIndicacaoNascimentoJn.setDescricao(indicacaoNascimentoOriginal.getDescricao());
		mcoIndicacaoNascimentoJn.setIndSituacao(indicacaoNascimentoOriginal.getIndSituacao());
		mcoIndicacaoNascimentoJn.setSerMatricula(indicacaoNascimentoOriginal.getSerMatricula());
		mcoIndicacaoNascimentoJn.setSerVinCodigo(indicacaoNascimentoOriginal.getSerVinCodigo());
		
		this.mcoIndicacaoNascimentoJnDAO.persistir(mcoIndicacaoNascimentoJn);
	}
}
