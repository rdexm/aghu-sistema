package br.gov.mec.aghu.sig.custos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.SigAtividadePessoaRestricoes;
import br.gov.mec.aghu.model.SigAtividadePessoas;
import br.gov.mec.aghu.sig.dao.SigAtividadePessoaRestricoesDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadePessoasDAO;

@Stateless
public class ManterPessoasAtividadeON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterPessoasAtividadeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SigAtividadePessoasDAO sigAtividadePessoasDAO;

@Inject
private SigAtividadePessoaRestricoesDAO sigAtividadePessoaRestricoesDAO;


	private static final long serialVersionUID = 294526316467150254L;

	public enum ManterPessoasAtividadeONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_GRUPO_OCUPACAO_JA_EXISTENTE_ATIVIDADE, MENSAGEM_DIRECIONADOR_NAO_INFORMADO, MENSAGEM_TEMPO_NAO_INFORMADO, MENSAGEM_TEMPO_QT_PROFISSIONAIS;
	}

	public void validarInclusaoPessoaAtividade(SigAtividadePessoas sigAtividadePessoas, List<SigAtividadePessoas> list)
			throws ApplicationBusinessException {
		this.validarGrupoOcupacao(sigAtividadePessoas, list);
		this.normalizarCamposNumericosObrigatorios(sigAtividadePessoas);
		this.validarTempoDirecionador(sigAtividadePessoas);
		this.validarTempoQtProfissionais(sigAtividadePessoas);
	}

	public void validarAlteracaoPessoaAtividade(SigAtividadePessoas sigAtividadePessoas, List<SigAtividadePessoas> list)
			throws ApplicationBusinessException {
		this.normalizarCamposNumericosObrigatorios(sigAtividadePessoas);
		this.validarTempoDirecionador(sigAtividadePessoas);
		this.validarTempoQtProfissionais(sigAtividadePessoas);
	}

	public void validarGrupoOcupacao(SigAtividadePessoas sigAtividadePessoas, List<SigAtividadePessoas> list) throws ApplicationBusinessException {
		for (SigAtividadePessoas itemLista : list) {
			if (sigAtividadePessoas.getSigGrupoOcupacoes().getSeq().equals(itemLista.getSigGrupoOcupacoes().getSeq())) {
				throw new ApplicationBusinessException(ManterPessoasAtividadeONExceptionCode.MENSAGEM_GRUPO_OCUPACAO_JA_EXISTENTE_ATIVIDADE);
			}
		}
	}

	public void validarTempoDirecionador(SigAtividadePessoas sigAtividadePessoas) throws ApplicationBusinessException {
		if (sigAtividadePessoas.getTempo() != null && sigAtividadePessoas.getSigDirecionadores() == null) {
			throw new ApplicationBusinessException(ManterPessoasAtividadeONExceptionCode.MENSAGEM_DIRECIONADOR_NAO_INFORMADO);
		} else if (sigAtividadePessoas.getTempo() == null && sigAtividadePessoas.getSigDirecionadores() != null) {
			throw new ApplicationBusinessException(ManterPessoasAtividadeONExceptionCode.MENSAGEM_TEMPO_NAO_INFORMADO);
		}
	}

	// #20126
	private void validarTempoQtProfissionais(SigAtividadePessoas sigAtividadePessoas) throws ApplicationBusinessException {
		if (sigAtividadePessoas.getTempo() != null && sigAtividadePessoas.getQuantidade() == null) {
			throw new ApplicationBusinessException(ManterPessoasAtividadeONExceptionCode.MENSAGEM_TEMPO_QT_PROFISSIONAIS);
		}
	}

	public void persistirPessoa(SigAtividadePessoas sigAtividadePessoas) {
		// alteração
		if (sigAtividadePessoas.getSeq() != null) {
			this.getSigAtividadePessoasDAO().atualizar(sigAtividadePessoas);
			// inclusao
		} else {
			this.getSigAtividadePessoasDAO().persistir(sigAtividadePessoas);
		}
		
		
		//Salva/Exclui as restrições
		for(SigAtividadePessoaRestricoes restricao : sigAtividadePessoas.getListAtividadePessoaRestricoes()){

			if(restricao.getSeq() == null && restricao.getPercentual() != null){
				restricao.setSigAtividadePessoas(sigAtividadePessoas);
				this.getSigAtividadePessoaRestricoesDAO().persistir(restricao);
			}
			else if (restricao.getSeq() != null && restricao.getPercentual() == null){
				this.getSigAtividadePessoaRestricoesDAO().removerPorId(restricao.getSeq());
			}
			else if (restricao.getSeq() != null && restricao.getPercentual() != null){
				this.getSigAtividadePessoaRestricoesDAO().atualizar(restricao);
			}
		}
		
		//Atualiza a lista do objeto após salvar
		sigAtividadePessoas.setListAtividadePessoaRestricoes(this.getSigAtividadePessoaRestricoesDAO().listarAtividadePessoaRestricoes(sigAtividadePessoas));
	}
	
	protected void normalizarCamposNumericosObrigatorios(SigAtividadePessoas sigAtividadePessoas){
		
		if(sigAtividadePessoas != null){
			//Uma vez que os campos Qtd Profissionais e Tempo Médio sejam obrigatórios, conforme regras de negócio, também deve ser impedido o registro destes campos com valor 0.
			if(sigAtividadePessoas.getTempo() != null && sigAtividadePessoas.getTempo() < 1){
				sigAtividadePessoas.setTempo(null);
			}
			
			//Uma vez que os campos Qtd Profissionais e Tempo Médio sejam obrigatórios, conforme regras de negócio, também deve ser impedido o registro destes campos com valor 0.
			if(sigAtividadePessoas.getQuantidade()!= null && sigAtividadePessoas.getQuantidade() < 1){
				sigAtividadePessoas.setQuantidade(null);
			}
		}
	}

	// DAOs
	protected SigAtividadePessoasDAO getSigAtividadePessoasDAO() {
		return sigAtividadePessoasDAO;
	}
	
	protected SigAtividadePessoaRestricoesDAO getSigAtividadePessoaRestricoesDAO() {
		return sigAtividadePessoaRestricoesDAO;
	}
}
