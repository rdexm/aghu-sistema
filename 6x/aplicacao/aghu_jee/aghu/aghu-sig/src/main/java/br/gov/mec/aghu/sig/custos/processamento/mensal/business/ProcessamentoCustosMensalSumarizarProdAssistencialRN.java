package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoProducao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.SumarioProducaoAssistencialPacienteVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Estória de Usuário #28203 - Sumarizar a Produção Assistencial do Paciente
 * @author rogeriovieira
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalSumarizarProdAssistencialRN.class)
public class ProcessamentoCustosMensalSumarizarProdAssistencialRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -1597990275338060162L;

	@Override
	public String getTitulo() {
		return "Sumarizar a produção assistencial do paciente";
	}

	@Override
	public String getNome() {
		return "ProcessamentoCustosMensalSumarizarProdAssistencialRN";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 25;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto,RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		
		// Passo 1 
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_SUMARIZACAO_PRODUCAO_ASSISTENCIAL_PACIENTE_INICIO");

		// Passo 2
		List<SumarioProducaoAssistencialPacienteVO> lista = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarProducaoPaciente(sigProcessamentoCusto);

		// Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_SUMARIZACAO_PRODUCAO_ASSISTENCIAL_PACIENTE_CONSULTA_SUCESSO");

		// Passo 4
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(lista)) {
			
			for (SumarioProducaoAssistencialPacienteVO vo : lista) {

				if (vo.getCentroCusto() != null && vo.getObjetoCustoVersao() != null) {

					// Passo 5 - Inserindo a producao
					SigDetalheProducao detalheProducao = new SigDetalheProducao();
					detalheProducao.setSigProcessamentoCustos(sigProcessamentoCusto);
					detalheProducao.setFccCentroCustos(vo.getCentroCusto());
					detalheProducao.setCriadoEm(new Date());
					detalheProducao.setRapServidores(rapServidores);
					detalheProducao.setGrupo(DominioGrupoDetalheProducao.PAC);
					detalheProducao.setTipoProducao(DominioTipoProducao.F);
					detalheProducao.setSigObjetoCustoVersoes(vo.getObjetoCustoVersao());
					detalheProducao.setFatProcedHospInternos(vo.getPhi());
					detalheProducao.setPagador(vo.getPagador());
					detalheProducao.setQtde(vo.getQtde());
	
					this.getProcessamentoCustoUtils().getSigDetalheProducaoDAO().persistir(detalheProducao);
					this.commitProcessamentoCusto();
				}
			}
		}

		// Passo 6
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_SUMARIZACAO_PRODUCAO_ASSISTENCIAL_PACIENTE_GARAVADOS_SUCESSO");
	}
}
