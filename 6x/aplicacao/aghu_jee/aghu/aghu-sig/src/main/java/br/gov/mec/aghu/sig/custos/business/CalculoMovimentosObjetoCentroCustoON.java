package br.gov.mec.aghu.sig.custos.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.dominio.DominioTipoVisaoAnalise;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.vo.CalculoObjetosCentrosCustosInterface;
import br.gov.mec.aghu.sig.custos.vo.DetalhamentoCustosGeralVO;
import br.gov.mec.aghu.sig.custos.vo.SomatorioAnaliseCustosObjetosCustoVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseOtimizacaoVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseCustosObjetosCustoVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseTabCustosObjetosCustoVO;
import br.gov.mec.aghu.sig.dao.SigCalculoObjetoCustoDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoObjetoCustoServicoDAO;
import br.gov.mec.aghu.sig.dao.SigMvtoContaMensalDAO;
import br.gov.mec.aghu.sig.dao.SigMvtoContaMensalServicoDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoCustoDAO;

@Stateless
public class CalculoMovimentosObjetoCentroCustoON extends BaseBusiness {

	private static final String DIRETOS = "Diretos";

	private static final Log LOG = LogFactory.getLog(CalculoMovimentosObjetoCentroCustoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private SigMvtoContaMensalDAO sigMvtoContaMensalDAO;

	@Inject
	private SigMvtoContaMensalServicoDAO sigMvtoContaMensalServicoDAO;

	@Inject
	private SigCalculoObjetoCustoDAO sigCalculoObjetoCustoDAO;

	@Inject
	private SigCalculoObjetoCustoServicoDAO sigCalculoObjetoCustoServicoDAO;
	
	@Inject
	private SigProcessamentoCustoDAO sigProcessamentoCustoDAO;

	private static final long serialVersionUID = -6304055715922619557L;
	
	public VisualizarAnaliseOtimizacaoVO buscarCustosVisaoCentroCustosOtimizacao(Integer seqCompetencia,
			FccCentroCustos fccCentroCustos, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao){
		
		VisualizarAnaliseOtimizacaoVO vo = new VisualizarAnaliseOtimizacaoVO();
		
		List<VisualizarAnaliseCustosObjetosCustoVO> lista = this.getSigProcessamentoCustoDAO().buscarCustosVisaoCentroCustosOtimizacao(seqCompetencia, fccCentroCustos, tiposCentroProducao);

		//Filtrar o centro de produção diretamente na aplicação
		if(sigCentroProducao != null){
			for (VisualizarAnaliseCustosObjetosCustoVO visualizarAnaliseCustosObjetosCustoVO : lista) {
				if(visualizarAnaliseCustosObjetosCustoVO.getSeqCentroProducao()!= null && visualizarAnaliseCustosObjetosCustoVO.getSeqCentroProducao().equals(sigCentroProducao.getSeq())){
					vo.getLista().add(visualizarAnaliseCustosObjetosCustoVO);
				}
			}
		}
		else{
			vo.setLista(lista);
		}
		
		if (!vo.getLista().isEmpty()) {
			
			SomatorioAnaliseCustosObjetosCustoVO somatorio = vo.getSomatorio();
			
			for (VisualizarAnaliseCustosObjetosCustoVO visualizarAnaliseCustosObjetosCustoVO: vo.getLista()) {
				somatorio.setSomatorioInsumos(somatorio.getSomatorioInsumos().add(visualizarAnaliseCustosObjetosCustoVO.getInsumos()));
				somatorio.setSomatorioPessoal(somatorio.getSomatorioPessoal().add(visualizarAnaliseCustosObjetosCustoVO.getPessoal()));
				somatorio.setSomatorioEquipamentos(somatorio.getSomatorioEquipamentos().add(visualizarAnaliseCustosObjetosCustoVO.getEquipamentos()));
				somatorio.setSomatorioServicos(somatorio.getSomatorioServicos().add(visualizarAnaliseCustosObjetosCustoVO.getServicos()));
				somatorio.setSomatorioDireto(somatorio.getSomatorioDireto().add(visualizarAnaliseCustosObjetosCustoVO.getValorDireto()));
				somatorio.setSomatorioIndiretos(somatorio.getSomatorioIndiretos().add(visualizarAnaliseCustosObjetosCustoVO.getValorIndireto()));
				somatorio.setSomatorioTotal(somatorio.getSomatorioTotal().add(visualizarAnaliseCustosObjetosCustoVO.getTotal()));
			}
		}
		return vo;
	}

	/**
	 * {@link ICustosSigCadastrosBasicosFacade#buscarMovimentosGeral(Integer, Integer, FccCentroCustos, DominioTipoVisaoAnalise)}
	 */
	public List<DetalhamentoCustosGeralVO> buscarMovimentosGeral(Integer pmuSeq, Integer ocvSeq, FccCentroCustos fccCentroCustos,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		List<DetalhamentoCustosGeralVO> list = new ArrayList<DetalhamentoCustosGeralVO>();

		DetalhamentoCustosGeralVO voDiretoInsumos = new DetalhamentoCustosGeralVO();
		DetalhamentoCustosGeralVO voDiretoPessoas = new DetalhamentoCustosGeralVO();
		DetalhamentoCustosGeralVO voDiretoEquipamentos = new DetalhamentoCustosGeralVO();
		DetalhamentoCustosGeralVO voDiretoServicos = new DetalhamentoCustosGeralVO();
		DetalhamentoCustosGeralVO voIndiretos = new DetalhamentoCustosGeralVO();

		switch (tipoVisaoAnaliseItens) {
		case OBJETO_CUSTO:
			List<SigCalculoObjetoCusto> lista = this.getSigCalculoObjetoCustoDAO().listarCalculoObjetoCusto(pmuSeq, ocvSeq, fccCentroCustos);

			if (lista != null) {
				for (SigCalculoObjetoCusto calculoObjetoCusto : lista) {

					voDiretoInsumos.setAlocadoAtividade(voDiretoInsumos.getAlocadoAtividade().add(calculoObjetoCusto.getVlrAtvInsumos()));
					voDiretoInsumos.setRateado(voDiretoInsumos.getRateado().add(calculoObjetoCusto.getVlrRateioInsumos()));
					voDiretoInsumos.setTotal(voDiretoInsumos.getTotal().add(
							calculoObjetoCusto.getVlrAtvInsumos().add(calculoObjetoCusto.getVlrRateioInsumos())));

					voDiretoPessoas.setAlocadoAtividade(voDiretoPessoas.getAlocadoAtividade().add(calculoObjetoCusto.getVlrAtvPessoal()));
					voDiretoPessoas.setRateado(voDiretoPessoas.getRateado().add(calculoObjetoCusto.getVlrRateioPessoas()));
					voDiretoPessoas.setTotal(voDiretoPessoas.getTotal().add(calculoObjetoCusto.getVlrAtvPessoal())
							.add(calculoObjetoCusto.getVlrRateioPessoas()));

					voDiretoEquipamentos.setAlocadoAtividade(voDiretoEquipamentos.getAlocadoAtividade().add(
							calculoObjetoCusto.getVlrAtvEquipamento()));
					voDiretoEquipamentos.setRateado(voDiretoEquipamentos.getRateado().add(calculoObjetoCusto.getVlrRateioEquipamentos()));
					voDiretoEquipamentos.setTotal(voDiretoEquipamentos.getTotal().add(calculoObjetoCusto.getVlrAtvEquipamento())
							.add(calculoObjetoCusto.getVlrRateioEquipamentos()));

					voDiretoServicos
							.setAlocadoAtividade(voDiretoServicos.getAlocadoAtividade().add(calculoObjetoCusto.getVlrAtvServicos()));
					voDiretoServicos.setRateado(voDiretoServicos.getRateado().add(calculoObjetoCusto.getVlrRateioServico()));
					voDiretoServicos.setTotal(voDiretoServicos.getTotal().add(calculoObjetoCusto.getVlrAtvServicos())
							.add(calculoObjetoCusto.getVlrRateioServico()));

					voIndiretos.setAlocadoAtividade(null);
					voIndiretos.setRateado(voIndiretos.getRateado().add(calculoObjetoCusto.getVlrRateioIndiretos()));
					voIndiretos.setTotal(voIndiretos.getTotal().add(calculoObjetoCusto.getVlrRateioIndiretos()));
				}
			}
			break;
		case CENTRO_CUSTO:
			voDiretoInsumos = this.getSigMvtoContaMensalDAO().buscarMvtoContaMensal(pmuSeq, fccCentroCustos, DominioTipoValorConta.DI);
			voDiretoPessoas = this.getSigMvtoContaMensalDAO().buscarMvtoContaMensal(pmuSeq, fccCentroCustos, DominioTipoValorConta.DP);
			voDiretoEquipamentos = this.getSigMvtoContaMensalDAO().buscarMvtoContaMensal(pmuSeq, fccCentroCustos, DominioTipoValorConta.DE);
			voDiretoServicos = this.getSigMvtoContaMensalDAO().buscarMvtoContaMensal(pmuSeq, fccCentroCustos, DominioTipoValorConta.DS);
			voIndiretos = this.getSigMvtoContaMensalDAO().buscarMvtoContaMensal(pmuSeq, fccCentroCustos, DominioTipoValorConta.II,
					DominioTipoValorConta.IS, DominioTipoValorConta.IE, DominioTipoValorConta.IP);
			break;
		}

		voDiretoInsumos.setTipoCusto(DIRETOS);
		voDiretoInsumos.setDespesa("Insumos");
		voDiretoPessoas.setTipoCusto(DIRETOS);
		voDiretoPessoas.setDespesa("Pessoas");
		voDiretoEquipamentos.setTipoCusto(DIRETOS);
		voDiretoEquipamentos.setDespesa("Equipamentos");
		voDiretoServicos.setTipoCusto(DIRETOS);
		voDiretoServicos.setDespesa("Serviços");
		voIndiretos.setTipoCusto("Indiretos");
		voIndiretos.setDespesa(" ");

		list.add(voDiretoInsumos);
		list.add(voDiretoPessoas);
		list.add(voDiretoEquipamentos);
		list.add(voDiretoServicos);
		list.add(voIndiretos);
		this.calcularPercentual(list);
		return list;
	}

	/**
	 * {@link ICustosSigCadastrosBasicosFacade#buscarMovimentosEquipamentos(Integer, Integer, Integer, DominioTipoVisaoAnalise)}
	 */
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosInsumos(Integer seqCompetencia, Integer seqObjetoVersao,
			Integer seqCentroCusto, DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		List<VisualizarAnaliseTabCustosObjetosCustoVO> listMovimentosInsumosVO = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();

		if (tipoVisaoAnaliseItens != null) {
			switch (tipoVisaoAnaliseItens) {
			case OBJETO_CUSTO:
				List<VisualizarAnaliseTabCustosObjetosCustoVO> buscaMovimentosInsumosObjetoCustoParte1 = this.getSigCalculoObjetoCustoDAO()
						.buscarMovimentosInsumosObjetoCustoParte1(seqCompetencia, seqObjetoVersao, seqCentroCusto);

				List<VisualizarAnaliseTabCustosObjetosCustoVO> buscaMovimentosInsumosObjetoCustoParte2 = this.getSigCalculoObjetoCustoDAO()
						.buscarMovimentosInsumosObjetoCustoParte2(seqCompetencia, seqObjetoVersao, seqCentroCusto);

				listMovimentosInsumosVO = this.juntarDuasListas(buscaMovimentosInsumosObjetoCustoParte1,
						buscaMovimentosInsumosObjetoCustoParte2);
				break;
			case CENTRO_CUSTO:
				listMovimentosInsumosVO = this.getSigMvtoContaMensalDAO()
						.buscarMovimentosInsumosCentroCusto(seqCompetencia, seqCentroCusto);
				break;
			}
			this.calcularPercentual(listMovimentosInsumosVO);
		}
		return listMovimentosInsumosVO;
	}

	/**
	 * {@link ICustosSigCadastrosBasicosFacade#buscarMovimentosEquipamentos(Integer, Integer, Integer, DominioTipoVisaoAnalise)}
	 */
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosEquipamentos(Integer seqCompetencia,
			Integer seqObjetoCustoVersao, Integer seqCentroCusto, DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		List<VisualizarAnaliseTabCustosObjetosCustoVO> listMovimentosEquipamentosVO = null;
		switch (tipoVisaoAnaliseItens) {
		case OBJETO_CUSTO:
			List<VisualizarAnaliseTabCustosObjetosCustoVO> buscaMovimentosEquipamentoObjetoCustoParte1 = this.getSigCalculoObjetoCustoDAO()
					.buscarMovimentosEquipamentoObjetoCustoParte1(seqCompetencia, seqObjetoCustoVersao, seqCentroCusto);
			List<VisualizarAnaliseTabCustosObjetosCustoVO> buscaMovimentosEquipamentoObjetoCustoParte2 = this.getSigCalculoObjetoCustoDAO()
					.buscarMovimentosEquipamentoObjetoCustoParte2(seqCompetencia, seqObjetoCustoVersao, seqCentroCusto);

			listMovimentosEquipamentosVO = this.juntarDuasListas(buscaMovimentosEquipamentoObjetoCustoParte1,
					buscaMovimentosEquipamentoObjetoCustoParte2);
			break;
		case CENTRO_CUSTO:
			listMovimentosEquipamentosVO = this.getSigMvtoContaMensalDAO().buscarMovimentosEquipamentoCentroCusto(seqCompetencia,
					seqCentroCusto);
			break;
		}

		this.calcularPercentual(listMovimentosEquipamentosVO);
		return listMovimentosEquipamentosVO;
	}

	/**
	 * {@link ICustosSigCadastrosBasicosFacade#buscarMovimentosServicos(Integer, Integer, Integer, DominioTipoVisaoAnalise)}
	 */
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosServicos(Integer seqCompetencia, Integer seqObjetoCustoVersao,
			Integer seqCentroCusto, DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {

		final Integer AUTOMATICO = 0;
		final Integer MANUAL = 1;
		final Integer AF = 2;

		List<VisualizarAnaliseTabCustosObjetosCustoVO> listMovimentosServicosVO = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();
		switch (tipoVisaoAnaliseItens) {
		case OBJETO_CUSTO:
			// Parte 1 -- Contratos automáticos assosicados as atividades
			List<VisualizarAnaliseTabCustosObjetosCustoVO> buscaMovimentosServicosObjetoCustoParte1 = this
					.getSigCalculoObjetoCustoServicoDAO().buscarMovimentosServicosObjetoCustoParte1(seqCompetencia, seqObjetoCustoVersao, seqCentroCusto, AUTOMATICO);

			// Parte 2 -- Contratos manuais assosicados as atividades
			List<VisualizarAnaliseTabCustosObjetosCustoVO> buscaMovimentosServicosObjetoCustoParte2 = this
					.getSigCalculoObjetoCustoServicoDAO().buscarMovimentosServicosObjetoCustoParte2(seqCompetencia, seqObjetoCustoVersao, seqCentroCusto,MANUAL);

			// Parte 3 -- AFs associados as atividades
			List<VisualizarAnaliseTabCustosObjetosCustoVO> buscaMovimentosServicosObjetoCustoParte3 = this
					.getSigCalculoObjetoCustoServicoDAO().buscarMovimentosServicosObjetoCustoParte3(seqCompetencia, seqObjetoCustoVersao, seqCentroCusto,AF);

			// Parte 4 -- Contratos automáticos rateados
			List<VisualizarAnaliseTabCustosObjetosCustoVO> buscaMovimentosServicosObjetoCustoParte4 = this
					.getSigCalculoObjetoCustoServicoDAO().buscarMovimentosServicosObjetoCustoParte4(seqCompetencia, seqObjetoCustoVersao, seqCentroCusto,AUTOMATICO);

			// Parte 5 -- Contratos manuais rateados
			List<VisualizarAnaliseTabCustosObjetosCustoVO> buscaMovimentosServicosObjetoCustoParte5 = this
					.getSigCalculoObjetoCustoServicoDAO().buscarMovimentosServicosObjetoCustoParte5(seqCompetencia, seqObjetoCustoVersao, seqCentroCusto,MANUAL);

			// Parte 6 -- Afs rateadas
			List<VisualizarAnaliseTabCustosObjetosCustoVO> buscaMovimentosServicosObjetoCustoParte6 = this
					.getSigCalculoObjetoCustoServicoDAO().buscarMovimentosServicosObjetoCustoParte6(seqCompetencia, seqObjetoCustoVersao, seqCentroCusto,AF);

			listMovimentosServicosVO.addAll(buscaMovimentosServicosObjetoCustoParte1);
			listMovimentosServicosVO.addAll(buscaMovimentosServicosObjetoCustoParte2);
			listMovimentosServicosVO.addAll(buscaMovimentosServicosObjetoCustoParte3);
			listMovimentosServicosVO.addAll(buscaMovimentosServicosObjetoCustoParte4);
			listMovimentosServicosVO.addAll(buscaMovimentosServicosObjetoCustoParte5);
			listMovimentosServicosVO.addAll(buscaMovimentosServicosObjetoCustoParte6);

			Collections.sort(listMovimentosServicosVO);
			break;
		case CENTRO_CUSTO:
			listMovimentosServicosVO.addAll(this.getSigMvtoContaMensalServicoDAO().buscarMovimentosServicosCentroCustoParte1(
					seqCompetencia, seqCentroCusto, 0));
			listMovimentosServicosVO.addAll(this.getSigMvtoContaMensalServicoDAO().buscarMovimentosServicosCentroCustoParte2(
					seqCompetencia, seqCentroCusto, 1));
			listMovimentosServicosVO.addAll(this.getSigMvtoContaMensalServicoDAO().buscarMovimentosServicosCentroCustoParte3(
					seqCompetencia, seqCentroCusto, 2));
			break;
		}

		this.calcularPercentual(listMovimentosServicosVO);
		return listMovimentosServicosVO;
	}

	/**
	 * {@link ICustosSigCadastrosBasicosFacade#buscaMovimentosPessoas(Integer, Integer, Integer, DominioTipoVisaoAnalise)}
	 */
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosPessoas(Integer seqCompetencia, Integer seqObjetoVersao,
			Integer seqCentroCusto, DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		List<VisualizarAnaliseTabCustosObjetosCustoVO> listMovimentosPessoasVO = null;

		switch (tipoVisaoAnaliseItens) {
		case OBJETO_CUSTO:
			List<VisualizarAnaliseTabCustosObjetosCustoVO> buscaMovimentosPessoasObjetoCustoParte1 = this.getSigMvtoContaMensalDAO()
					.buscarMovimentosPessoasObjetoCustoParte1(seqCompetencia, seqObjetoVersao, seqCentroCusto);
			List<VisualizarAnaliseTabCustosObjetosCustoVO> buscaMovimentosPessoasObjetoCustoParte2 = this.getSigMvtoContaMensalDAO()
					.buscarMovimentosPessoasObjetoCustoParte2(seqCompetencia, seqObjetoVersao, seqCentroCusto);

			this.inserirDemaisOcupacoes(buscaMovimentosPessoasObjetoCustoParte1);
			this.inserirDemaisOcupacoes(buscaMovimentosPessoasObjetoCustoParte2);

			listMovimentosPessoasVO = this.juntarDuasListas(buscaMovimentosPessoasObjetoCustoParte1,
					buscaMovimentosPessoasObjetoCustoParte2);
			break;
		case CENTRO_CUSTO:
			listMovimentosPessoasVO = this.getSigMvtoContaMensalDAO().buscarMovimentosPessoasCentroCusto(seqCompetencia, seqCentroCusto);
			this.inserirDemaisOcupacoes(listMovimentosPessoasVO);
			break;
		}

		this.calcularPercentual(listMovimentosPessoasVO);
		return listMovimentosPessoasVO;
	}

	private void inserirDemaisOcupacoes(List<VisualizarAnaliseTabCustosObjetosCustoVO> listMovimentosPessoasVO) {
		for (VisualizarAnaliseTabCustosObjetosCustoVO visualizarAnaliseTabCustosObjetosCustoVO : listMovimentosPessoasVO) {
			if (visualizarAnaliseTabCustosObjetosCustoVO.getDescricao() == null
					|| visualizarAnaliseTabCustosObjetosCustoVO.getDescricao().equals("")) {
				visualizarAnaliseTabCustosObjetosCustoVO.setDescricao("Demais Ocupações");
			}
		}
	}

	/**
	 * {@link ICustosSigCadastrosBasicosFacade#buscarMovimentosIndiretos(Integer, Integer, Integer, DominioTipoVisaoAnalise)}
	 */
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosIndiretos(Integer seqCompetencia, Integer seqObjetoVersao,
			Integer seqCentroCusto, DominioTipoVisaoAnalise tipoVisaoAnaliseItens, Integer codigoDebita) {
		List<VisualizarAnaliseTabCustosObjetosCustoVO> listMovimentosInsumosVO = null;

		switch (tipoVisaoAnaliseItens) {
		case OBJETO_CUSTO:
			List<VisualizarAnaliseTabCustosObjetosCustoVO> parte1 = this.getSigCalculoObjetoCustoDAO()
					.buscarCustosIndiretosObjetoCustoParte1(seqCompetencia, seqObjetoVersao, seqCentroCusto, codigoDebita);
			List<VisualizarAnaliseTabCustosObjetosCustoVO> parte2 = this.getSigCalculoObjetoCustoDAO()
					.buscarCustosIndiretosObjetoCustoParte2(seqCompetencia, seqObjetoVersao, seqCentroCusto, codigoDebita);
			List<VisualizarAnaliseTabCustosObjetosCustoVO> parte3 = this.getSigCalculoObjetoCustoDAO()
					.buscarCustosIndiretosObjetoCustoParte3(seqCompetencia, seqObjetoVersao, seqCentroCusto, codigoDebita);
			List<VisualizarAnaliseTabCustosObjetosCustoVO> parte4 = this.getSigCalculoObjetoCustoDAO()
					.buscarCustosIndiretosObjetoCustoParte4(seqCompetencia, seqObjetoVersao, seqCentroCusto, codigoDebita);

			listMovimentosInsumosVO = this.juntarDuasListasIndiretos(this.juntarDuasListasIndiretos(parte3, parte4),
					this.juntarDuasListasIndiretos(parte1, parte2));

			for (VisualizarAnaliseTabCustosObjetosCustoVO visualizarAnaliseTabCustosObjetosCustoVO : listMovimentosInsumosVO) {
				visualizarAnaliseTabCustosObjetosCustoVO.setTotal(ProcessamentoCustoUtils
						.criarBigDecimal(visualizarAnaliseTabCustosObjetosCustoVO.getTotalInsumos()
								+ visualizarAnaliseTabCustosObjetosCustoVO.getTotalPessoas()
								+ visualizarAnaliseTabCustosObjetosCustoVO.getTotalEquipamentos()
								+ visualizarAnaliseTabCustosObjetosCustoVO.getTotalServicos()));
			}

			break;
		case CENTRO_CUSTO:
			listMovimentosInsumosVO = this.getSigMvtoContaMensalDAO().buscarMovimentosIndiretosCentroCusto(seqCompetencia, seqCentroCusto, codigoDebita);
			break;
		}

		this.calcularPercentual(listMovimentosInsumosVO);
		return listMovimentosInsumosVO;
	}

	private List<VisualizarAnaliseTabCustosObjetosCustoVO> juntarDuasListasIndiretos(List<VisualizarAnaliseTabCustosObjetosCustoVO> lista1,
			List<VisualizarAnaliseTabCustosObjetosCustoVO> lista2) {
		HashMap<String, VisualizarAnaliseTabCustosObjetosCustoVO> hash = new HashMap<String, VisualizarAnaliseTabCustosObjetosCustoVO>();

		if (!ProcessamentoCustoUtils.verificarListaNaoVazia(lista1)) {
			if (!ProcessamentoCustoUtils.verificarListaNaoVazia(lista2)) {
				return new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();
			} else {
				return lista1;
			}
		}

		if (!ProcessamentoCustoUtils.verificarListaNaoVazia(lista2)) {
			return lista1;
		}

		for (VisualizarAnaliseTabCustosObjetosCustoVO parte1 : lista1) {
			String chave = parte1.getDescricao() + parte1.getNome()+ parte1.getIteracao(); 
			hash.put(chave, parte1);
		}

		for (VisualizarAnaliseTabCustosObjetosCustoVO parte2 : lista2) {
			String chave = parte2.getDescricao() + parte2.getNome() + parte2.getIteracao(); 
			if (hash.get(chave) == null) {
				hash.put(chave, parte2);
			} else {
				VisualizarAnaliseTabCustosObjetosCustoVO parte1 = hash.get(chave);
				parte1.setTotalInsumos(parte1.getTotalInsumos() + parte2.getTotalInsumos());
				parte1.setTotalPessoas(parte1.getTotalPessoas() + parte2.getTotalPessoas());
				parte1.setTotalEquipamentos(parte1.getTotalEquipamentos() + parte2.getTotalEquipamentos());
				parte1.setTotalServicos(parte1.getTotalServicos() + parte2.getTotalServicos());
			}
		}

		List<VisualizarAnaliseTabCustosObjetosCustoVO> listMovimentosPessoasVO = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>(
				hash.values());
		Collections.sort(listMovimentosPessoasVO);
		return listMovimentosPessoasVO;
	}

	/**
	 * Método genérico para clacular o percentual, é necessário que a lista seja
	 * de objetos que implementem a interface
	 * {@link CalculoObjetosCentrosCustosInterface}
	 * 
	 * @author rmalvezzi
	 * @param vos
	 *            Lista a ser calculada o percentual
	 */
	private <T extends CalculoObjetosCentrosCustosInterface> void calcularPercentual(List<T> vos) {
		BigDecimal totalObjeto = BigDecimal.ZERO;
		for (CalculoObjetosCentrosCustosInterface vo : vos) {
			totalObjeto = totalObjeto.add(vo.getTotal());
		}
		for (CalculoObjetosCentrosCustosInterface vo : vos) {
			if (vo.getTotal().compareTo(BigDecimal.ZERO) > 0) {
				vo.setPercentual(ProcessamentoCustoUtils.dividir(vo.getTotal().multiply(ProcessamentoCustoUtils.criarBigDecimal(100.0)),
						totalObjeto).doubleValue());
			} else {
				vo.setPercentual(0D);
			}
		}
	}

	/**
	 * Junta as duas listas passadas por parametros, se elas possuirem algum
	 * item igual (mesma descrição), o merge dos itens é feita apenas para os
	 * valores quantidade e totalRateado.
	 * 
	 * @author rmalvezzi
	 * @param lista1
	 *            Lista principal.
	 * @param lista2
	 *            Lista secundaria.
	 * @return Lista ordenada pela descrição.
	 */
	private List<VisualizarAnaliseTabCustosObjetosCustoVO> juntarDuasListas(List<VisualizarAnaliseTabCustosObjetosCustoVO> lista1,
			List<VisualizarAnaliseTabCustosObjetosCustoVO> lista2) {

		if (lista1 == null) {
			return lista2;
		}

		if (lista2 == null) {
			return lista1;
		}

		List<VisualizarAnaliseTabCustosObjetosCustoVO> listaAtualizada = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();

		listaAtualizada.addAll(lista1);
		listaAtualizada.addAll(lista2);
		/*
		 * for (int i = 0; i < lista2.size(); i++) {
		 * 
		 * VisualizarAnaliseTabCustosObjetosCustoVO voAtualDaLista2 =
		 * lista2.get(i);
		 * 
		 * if(!lista1.contains(voAtualDaLista2)){ lista1.add(voAtualDaLista2);
		 * 
		 * }else{ VisualizarAnaliseTabCustosObjetosCustoVO
		 * voJaExisteNaLista1MasEhAtualizado = lista1.get(i);
		 * voJaExisteNaLista1MasEhAtualizado
		 * .setQuantidade(voAtualDaLista2.getQuantidade());
		 * voJaExisteNaLista1MasEhAtualizado
		 * .setTotalRateado(voAtualDaLista2.getTotalRateado()); } }
		 */
		Collections.sort(listaAtualizada);
		return listaAtualizada;
	}

	// DAOs
	protected SigCalculoObjetoCustoDAO getSigCalculoObjetoCustoDAO() {
		return sigCalculoObjetoCustoDAO;
	}

	protected SigCalculoObjetoCustoServicoDAO getSigCalculoObjetoCustoServicoDAO() {
		return sigCalculoObjetoCustoServicoDAO;
	}

	protected SigMvtoContaMensalDAO getSigMvtoContaMensalDAO() {
		return sigMvtoContaMensalDAO;
	}

	protected SigMvtoContaMensalServicoDAO getSigMvtoContaMensalServicoDAO() {
		return sigMvtoContaMensalServicoDAO;
	}

	public SigProcessamentoCustoDAO getSigProcessamentoCustoDAO() {
		return sigProcessamentoCustoDAO;
	}
}