package br.gov.mec.aghu.sig.custos.processamento.business;

import java.util.List;
import java.util.Map;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigCategoriaRecurso;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


@TransactionManagement(TransactionManagementType.BEAN)
public abstract class ProcessamentoDiarioContagemBusiness extends ProcessamentoCustoBusiness {

	private static final long serialVersionUID = 1796794283526044782L;
	private static final Log LOG = LogFactory.getLog(ProcessamentoDiarioContagemBusiness.class);
	
	/**
	 * O método principal da contagem, o responsavel em realizar o que a etapa em si precisa fazer.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor logado.
	 * @param sigProcessamentoPassos				Representação do Passo atual.
	 * @param tipoObjetoCusto						Tipo do objeto de custo, Assitencial ou Apoio.
	 * @throws AGHUNegocioExceptionSemRollback		Exceção lançada se algum erro acontecer na hora do commit do processamento. 
	 */
	public void executarContagem(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdPaciente calculoAtdPaciente, List<AinMovimentosInternacao> movimentosInternacoes, 
			Map<DominioIndContagem, SigCategoriaConsumos> categoriasConsumos, Map<Integer, SigCategoriaRecurso> categoriasRecursos, Map<AghuParametrosEnum, Object> parametros,  Boolean alta) throws ApplicationBusinessException{
		try{
			this.iniciarProcessamentoCusto();
			this.gravarLogDebugProcessamento("Início ATD_SEQ["+calculoAtdPaciente.getAtendimento().getSeq()+"]"+this.getClass().getSimpleName(), sigProcessamentoCusto, rapServidores, DominioEtapaProcessamento.G, sigProcessamentoPassos);
			this.executarPassosInternos( sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, calculoAtdPaciente, movimentosInternacoes, categoriasConsumos, categoriasRecursos, parametros, alta);
			this.gravarLogDebugProcessamento("Sucesso "+this.getClass().getSimpleName(), sigProcessamentoCusto, rapServidores, DominioEtapaProcessamento.G, sigProcessamentoPassos);
			this.finalizarProcessamentoCusto();
		}
		catch(Exception e){
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_ETAPA_CONTAGEM_DIARIA, e);
		}
	}
	
	/**
	 * Método responsável pela execução dos passos internos da contagem
	 * 
	 * @param sigProcessamentoCusto
	 * @param rapServidores
	 * @param sigProcessamentoPassos
	 * @param calculoAtdPaciente
	 * @param alta
	 * @throws ApplicationBusinessException
	 */
	protected abstract void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdPaciente calculoAtdPaciente, List<AinMovimentosInternacao> movimentosInternacoes,
			Map<DominioIndContagem, SigCategoriaConsumos> categoriasConsumos, Map<Integer, SigCategoriaRecurso> categoriasRecursos, Map<AghuParametrosEnum, Object> parametros,  Boolean alta) throws ApplicationBusinessException;
}