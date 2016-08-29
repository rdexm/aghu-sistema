package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.hibernate.ScrollableResults;

import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoProducao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.ProducaoExameVO;
import br.gov.mec.aghu.sig.dao.SigDetalheProducaoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe de execução da contabilização da produção de exames do mês.
 * 
 * #16834
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalContabilizacaoProducaoExameRN.class)
public class ProcessamentoCustosMensalContabilizacaoProducaoExameRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -5811664870339719784L;

	@Override
	public String getTitulo() {
		return "Contabilizacao da producao de exames do mes.";
	}

	@Override
	public String getNome() {
		return "processamentoMensalContabilizacaoProducaoExameON - processamentoInsumosEtapa6";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 6;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		this.processamentoInsumosEtapa5(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
	}

	/**
	 * Método principal da Etapa 6 do processamento, é a responsável em disparar
	 * todos os passos dessa etapa.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto
	 *            Processamento Atual.
	 * @param rapServidores
	 *            Servidor Logado.
	 * @param sigProcessamentoPassos
	 *            Passo Atual.
	 * @throws ApplicationBusinessException
	 *             Exceção lançada se alguma coisa aconteceu na hora do commit
	 *             do processamento.
	 */
	public void processamentoInsumosEtapa5(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		ICentroCustoFacade centroCustoFacade = this.getProcessamentoCustoUtils().getCentroCustoFacade();
		IFaturamentoFacade faturamentoFacade = this.getProcessamentoCustoUtils().getFaturamentoFacade();
		SigDetalheProducaoDAO sigDetalheProducaoDAO = this.getProcessamentoCustoUtils().getSigDetalheProducaoDAO();

		// Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_INICIO_BUSCA_PRODUCAO_EXAMES");

		// Passo 2

		ScrollableResults retorno = sigDetalheProducaoDAO.buscarProducaoExamesPorMesCompetencia(sigProcessamentoCusto);

		// Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_SUCESSO_BUSCA_PRODUCAO_EXAMES");

		if (retorno != null) {
			FatProcedHospInternos fatProcedHospInternosCorrente = null;
			FccCentroCustos fccCentroCustoCorrente = null;

			int somatorioQtdeExames = 0;

			Boolean retornouResultado = false;

			while (retorno.next()) {

				retornouResultado = true;

				ProducaoExameVO producaoExameVO = ProducaoExameVO.create((Object[]) retorno.get());

				// Passo 5
				FccCentroCustos fccCentroCustos = centroCustoFacade.obterCentroCustoPorChavePrimaria(producaoExameVO.getCctCodigo());

				FatProcedHospInternos fatProcedHospInternos = faturamentoFacade.obterProcedimentoHospitalarInterno(producaoExameVO
						.getPhiSeq());

				if (fatProcedHospInternosCorrente == null || fccCentroCustoCorrente == null) {
					fccCentroCustoCorrente = fccCentroCustos;
					fatProcedHospInternosCorrente = fatProcedHospInternos;
				}

				if (!fatProcedHospInternosCorrente.equals(fatProcedHospInternos) || !fccCentroCustoCorrente.equals(fccCentroCustos)) {
					this.insereSomatorio(sigDetalheProducaoDAO, fccCentroCustoCorrente, fatProcedHospInternosCorrente, rapServidores,
							sigProcessamentoCusto, somatorioQtdeExames);
					somatorioQtdeExames = 0;
					fccCentroCustoCorrente = fccCentroCustos;
					fatProcedHospInternosCorrente = fatProcedHospInternos;
				}

				// Passo 4
				SigDetalheProducao sigDetalheProducao = new SigDetalheProducao();
				sigDetalheProducao.setSigProcessamentoCustos(sigProcessamentoCusto);
				sigDetalheProducao.setFccCentroCustos(fccCentroCustos);
				sigDetalheProducao.setCriadoEm(new Date());
				sigDetalheProducao.setRapServidores(rapServidores);
				sigDetalheProducao.setGrupo(DominioGrupoDetalheProducao.ORIG);
				sigDetalheProducao.setTipoProducao(DominioTipoProducao.valueOf(producaoExameVO.getOrigem()));
				sigDetalheProducao.setFatProcedHospInternos(fatProcedHospInternos);
				sigDetalheProducao.setNroDiasProducao(producaoExameVO.getNroDiasProducao());
				sigDetalheProducao.setQtde(new BigDecimal(producaoExameVO.getQtdExames()));

				sigDetalheProducaoDAO.persistir(sigDetalheProducao);

				somatorioQtdeExames += producaoExameVO.getQtdExames().intValue();
			}
			retorno.close();

			if (retornouResultado) {
				this.insereSomatorio(sigDetalheProducaoDAO, fccCentroCustoCorrente, fatProcedHospInternosCorrente, rapServidores,
						sigProcessamentoCusto, somatorioQtdeExames);
			}
			this.commitProcessamentoCusto();
		}

		// Passo 6
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_SUCESSO_GRAVACAO_PRODUCAO_EXAMES");
	}

	// Passo 5
	private void insereSomatorio(SigDetalheProducaoDAO sigDetalheProducaoDAO, FccCentroCustos fccCentroCustoCorrente,
			FatProcedHospInternos fatProcedHospInternosCorrente, RapServidores rapServidores, SigProcessamentoCusto sigProcessamentoCusto,
			Integer somatorioQtdeExames) {
		SigDetalheProducao sigDetalheProducaoSomatorio = new SigDetalheProducao();
		sigDetalheProducaoSomatorio.setSigProcessamentoCustos(sigProcessamentoCusto);
		sigDetalheProducaoSomatorio.setRapServidores(rapServidores);
		sigDetalheProducaoSomatorio.setFccCentroCustos(fccCentroCustoCorrente);
		sigDetalheProducaoSomatorio.setFatProcedHospInternos(fatProcedHospInternosCorrente);
		sigDetalheProducaoSomatorio.setGrupo(DominioGrupoDetalheProducao.PHI);
		sigDetalheProducaoSomatorio.setCriadoEm(new Date());
		sigDetalheProducaoSomatorio.setQtde(new BigDecimal(somatorioQtdeExames));
		sigDetalheProducaoDAO.persistir(sigDetalheProducaoSomatorio);

	}
}
