package br.gov.mec.aghu.exames.coleta.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioIndImpressoLaudo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.coleta.vo.PesquisaSolicitacaoDiversosFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.pesquisa.vo.VAelExamesAtdDiversosFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.VAelExamesAtdDiversosVO;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SolicitacaoDiversosON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SolicitacaoDiversosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IExamesFacade examesFacade;

@EJB
private IPesquisaExamesFacade pesquisaExamesFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8192107665871955888L;


	public enum SolicitacaoDiversosONExceptionCode implements BusinessExceptionCode {
		MESSAGE_SOLICITACAO_DIVERSOS_CAMPO_OBRIGATORIO, MESSAGE_SOLICITACAO_DIVERSOS_INTERVALO_DATA;
	} 
	
	
	public List<VAelExamesAtdDiversosVO> pesquisarSolicitacaoDiversos(PesquisaSolicitacaoDiversosFiltroVO filtro) throws ApplicationBusinessException {
		
		// - Não realizar pesquisa se nenhuma suggestion de 1 à 5 estiver preenchida.
		// - Não realizar pesquisa se mais que uma suggestion de 1 à 5 estiver preenchida.
		
		
		if(quantidadeSuggestionsSelecionadas(filtro) != 1 && 
				!verificarCamposPreenchidos(filtro)) { //melhoria #27381
			throw new ApplicationBusinessException(SolicitacaoDiversosONExceptionCode.MESSAGE_SOLICITACAO_DIVERSOS_CAMPO_OBRIGATORIO);
		}
		
		//Regra de datas somente se nao digitou solicitação #22254
//		if(filtro.getSolicitacao() == null && DateUtil.diffInDaysInteger(filtro.getDtFinal(), filtro.getDtInicio()) > 60) {
//			throw new ApplicationBusinessException(SolicitacaoDiversosONExceptionCode.MESSAGE_SOLICITACAO_DIVERSOS_INTERVALO_DATA);
//		} 
		// Removido por melhoria #27381
		
		final List<VAelExamesAtdDiversosVO> vos = this.getPesquisaExamesFacade().buscarVAelExamesAtdDiversos(criarFiltro(filtro));
		buscarUsuarioAlteracaoESituacao(vos);
		
		return vos;
		
	}
	
	/**
	 * Verificar se os campos Numero AP, Paciente e Solicitação foram preenchidos
	 * @param filtro
	 * @return
	 */
	private Boolean verificarCamposPreenchidos(
			PesquisaSolicitacaoDiversosFiltroVO filtro) {
		if(filtro.getNumeroAp() != null || filtro.getCodPaciente() != null || filtro.getSolicitacao() != null){
			return true;
		}
		return false;
	}

	private VAelExamesAtdDiversosFiltroVO criarFiltro(PesquisaSolicitacaoDiversosFiltroVO filtro) {
		final VAelExamesAtdDiversosFiltroVO filtroVO = new VAelExamesAtdDiversosFiltroVO();
		if (filtro.getDoador() != null) {
			filtroVO.setCadSeq(filtro.getDoador().getSeq());
		}
		if (filtro.getControleQualidade() != null) {
			filtroVO.setCcqSeq(filtro.getControleQualidade().getSeq());
		}
		if (filtro.getCadaver() != null) {
			filtroVO.setDdvSeq(filtro.getCadaver().getSeq());
		}
		if (filtro.getLaboratorioExterno() != null) {
			filtroVO.setLaeSeq(filtro.getLaboratorioExterno().getSeq());
		}
		if (filtro.getProjetoPesquisa() != null) {
			filtroVO.setPjqSeq(filtro.getProjetoPesquisa().getSeq());
		}
		if (filtro.getSituacao() != null) {
			filtroVO.setSitCodigo(filtro.getSituacao().getCodigo());
		}
		filtroVO.setPacCodigo(filtro.getCodPaciente());
		filtroVO.setAtvPacCodigo(filtro.getCodPaciente());
		filtroVO.setSoeSeq(filtro.getSolicitacao());
		filtroVO.setNumeroAp(filtro.getNumeroAp());
		filtroVO.setSomenteLaboratorial(DominioSimNao.S.equals(filtro.getSomenteLaboratorial()));

		if(DominioSimNao.S.equals(filtro.getImpressoLaudo())) {
			filtroVO.setIndImpressoLaudo(new DominioIndImpressoLaudo[] {DominioIndImpressoLaudo.S});
		} else if(DominioSimNao.N.equals(filtro.getImpressoLaudo())) {
			filtroVO.setIndImpressoLaudo(new DominioIndImpressoLaudo[] {DominioIndImpressoLaudo.N, DominioIndImpressoLaudo.A});
		}
		
		final IParametroFacade parametroFacade = getParametroFacade();
		if(DominioSimNao.N.equals(filtro.getMostraExamesCancelados())){
			filtroVO.setSitCodigoCancelado(parametroFacade.getAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO).getVlrTexto());
		}
		
		filtroVO.setSitCodigoPendente(parametroFacade.getAghParametro(AghuParametrosEnum.P_SITUACAO_PENDENTE).getVlrTexto());
		
		filtroVO.setDtInicio(filtro.getDtInicio());
		filtroVO.setDtFinal(filtro.getDtFinal());
		if (filtro.getConfigExLaudoUnico() != null) {
			filtroVO.setLu2Seq(filtro.getConfigExLaudoUnico().getSeq());
		}
		return filtroVO;
	}

	private void buscarUsuarioAlteracaoESituacao(List<VAelExamesAtdDiversosVO> vos) {
		final IExamesFacade examesFacade = this.getExamesFacade();
		final IRegistroColaboradorFacade registroColaboradorFacade = this.getRegistroColaboradorFacade();
		
		for (VAelExamesAtdDiversosVO vAelExamesAtdDiversosVO : vos) {
			if (vAelExamesAtdDiversosVO.getSitCodigo() != null) {
				vAelExamesAtdDiversosVO.setSitDescricao(examesFacade.obterSituacaoExamePeloId(vAelExamesAtdDiversosVO.getSitCodigo()).getDescricao());
			}
			if (DominioIndImpressoLaudo.A.equals(vAelExamesAtdDiversosVO.getIndImpressoLaudo()) && vAelExamesAtdDiversosVO.getSerMatricula() != null
					&& vAelExamesAtdDiversosVO.getSerVinCodigo() != null) {
				vAelExamesAtdDiversosVO.setAlterado(registroColaboradorFacade
						.buscaServidor(
								new RapServidoresId(vAelExamesAtdDiversosVO
										.getSerMatricula(),
										vAelExamesAtdDiversosVO
												.getSerVinCodigo()))
						.getUsuario());
			}
		}
	}
	
	public List<PesquisaExamesPacientesResultsVO> popularListaImpressaoLaudo(List<VAelExamesAtdDiversosVO> listaSolicitacaoDiversos) {
		List<PesquisaExamesPacientesResultsVO> result = new ArrayList<PesquisaExamesPacientesResultsVO>();
		
		for(VAelExamesAtdDiversosVO diverso : listaSolicitacaoDiversos) {
			PesquisaExamesPacientesResultsVO item = new PesquisaExamesPacientesResultsVO();
			item.setCodigoSoe(diverso.getSoeSeq());
			item.setIseSeq(diverso.getSeqp());
			item.setDtHrProgramada(diverso.getDthrProgramada());
			item.setDtHrEvento(diverso.getDataHoraEvento());
			item.setSituacaoCodigo(diverso.getSitCodigo());
			if(diverso.getTipoColeta() != null) {
				item.setTipoColeta(diverso.getTipoColeta().getDescricao());
			}
			item.setOrigemAtendimento(diverso.getOrigem());
			item.setExame(diverso.getDescricaoUsualExame());
			item.setMaterialAnalise(diverso.getDescricaoMaterial());
			if(diverso.getProntuario() != null && !diverso.getProntuario().isEmpty()) {
				item.setProntuario(Integer.parseInt(diverso.getProntuario().replace("/", "")));
			}
			item.setCodPaciente(diverso.getPacCodigo());
			item.setPaciente(diverso.getNomePaciente());
			result.add(item);
		}
		
		return result;
	}
	
	private int quantidadeSuggestionsSelecionadas(PesquisaSolicitacaoDiversosFiltroVO filtro){
		int suggestionSelecionadas = 0;
		if(filtro.getProjetoPesquisa() != null) {
			suggestionSelecionadas++;
		}
		if(filtro.getLaboratorioExterno() != null) {
			suggestionSelecionadas++;
		}
		if(filtro.getControleQualidade() != null) {
			suggestionSelecionadas++;
		}
		if(filtro.getCadaver() != null) {
			suggestionSelecionadas++;
		}
		if(filtro.getDoador() != null) {
			suggestionSelecionadas++;
		}
		return suggestionSelecionadas;
	}
	
	protected IPesquisaExamesFacade getPesquisaExamesFacade() {
		return this.pesquisaExamesFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	private IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
