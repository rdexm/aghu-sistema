package br.gov.mec.aghu.perinatologia.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.McoIndicacaoNascimento;
import br.gov.mec.aghu.perinatologia.dao.McoIndicacaoNascimentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
/**
 * @author israel.haas
 */
@Stateless
public class ManterCadastroIndicacaoNascimentoON extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;

	@Inject
	private McoIndicacaoNascimentoDAO mcoIndicacaoNascimentoDAO;
	
	@EJB
	private ManterCadastroIndicacaoNascimentoRN manterCadastroIndicacaoNascimentoRN;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum ManterCadastroIndicacaoNascimentoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_INDICACAO_JA_CADASTRADA
	}
	
	public void persistirIndicacaoNascimento(McoIndicacaoNascimento indicacaoNascimento,
			McoIndicacaoNascimento indicacaoNascimentoOriginal) throws BaseException {
		
		if (indicacaoNascimento.getCriadoEm() == null) {
			if (this.mcoIndicacaoNascimentoDAO.pesquisarIndicacaoPorDescricaoExata(indicacaoNascimento.getDescricao())) {
				throw new ApplicationBusinessException(ManterCadastroIndicacaoNascimentoONExceptionCode.MENSAGEM_INDICACAO_JA_CADASTRADA);
			}
			this.manterCadastroIndicacaoNascimentoRN.inserirIndicacaoNascimento(indicacaoNascimento);
			
		} else {
			this.manterCadastroIndicacaoNascimentoRN.atualizarIndicacaoNascimento(indicacaoNascimento, indicacaoNascimentoOriginal);
		}
	}
}
