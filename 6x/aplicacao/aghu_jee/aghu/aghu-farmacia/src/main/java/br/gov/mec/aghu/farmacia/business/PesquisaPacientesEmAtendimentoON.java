package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.vo.MedicoResponsavelVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.vo.PacientesEmAtendimentoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PesquisaPacientesEmAtendimentoON extends BaseBusiness implements Serializable{

	private static final Log LOG = LogFactory.getLog(PesquisaPacientesEmAtendimentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}

	private static final long serialVersionUID = -9113545663898632790L;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	public List<AghUnidadesFuncionais> pesquisarPorDescricaoCodigoAtivaAssociada(String strPesquisa) {
		return getAghuFacade()
		.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas( 
				strPesquisa,
				DominioSituacao.A,
				Boolean.TRUE,
				Boolean.TRUE,
				Boolean.TRUE,
				Arrays.asList(AghUnidadesFuncionais.Fields.ANDAR,
						AghUnidadesFuncionais.Fields.ALA,
						AghUnidadesFuncionais.Fields.DESCRICAO),
				ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO,
				ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA,
				ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA);
	}

	public Long pesquisarPorDescricaoCodigoAtivaAssociadaCount(String strPesquisa) {
		return getAghuFacade()
		.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
				strPesquisa,
				DominioSituacao.A,
				Boolean.TRUE,
				Boolean.TRUE,
				Boolean.TRUE,
				ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO,
				ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA,
				ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA);
	}
	
	public List<MedicoResponsavelVO> pesquisarMedicoResponsavel(String strPesquisa,Integer matriculaResponsavel, Short vinCodigoResponsavel) {
		List<MedicoResponsavelVO> listRetorno = new ArrayList<MedicoResponsavelVO>();
		
		List<Object[]> listaObjs = getAghuFacade().obterProfessoresInternacaoTodos(strPesquisa, matriculaResponsavel, vinCodigoResponsavel); 
		
		for (Object[] obj : listaObjs) {
			MedicoResponsavelVO linha = new MedicoResponsavelVO();
			linha.setMatricula((Integer)obj[0]);
			linha.setVinCodigo((Short)obj[1]);
			linha.setNome((String)obj[2]);
			linha.setNrgRegConselho((String)obj[3]);
			linha.setSigla((String)obj[4]);
			
			listRetorno.add(linha);
		}
		return listRetorno;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	public List<PacientesEmAtendimentoVO> listarPacientesEmAtendimento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer pacCodigo, String leito, Short unfSeq,
			Integer matriculaResp, Short vinCodigoResp,
			DominioOrigemAtendimento origem, Short espSeq) throws ApplicationBusinessException {
		List<PacientesEmAtendimentoVO> listRetorno = new ArrayList<PacientesEmAtendimentoVO>();
		listRetorno = criarListaPacientesEmAtendimento(pacCodigo, leito, unfSeq, matriculaResp, vinCodigoResp, origem, espSeq);

		CoreUtil.ordenarLista(listRetorno, PacientesEmAtendimentoVO.Fields.ESP_SIGLA.toString(), true);
		CoreUtil.ordenarLista(listRetorno, PacientesEmAtendimentoVO.Fields.ESP_SEQ.toString(), true);
		CoreUtil.ordenarLista(listRetorno, PacientesEmAtendimentoVO.Fields.UNF_SEQ.toString(), true);
		CoreUtil.ordenarLista(listRetorno, PacientesEmAtendimentoVO.Fields.MATRICULA_RESP.toString(), false);
		
		//Paginação
		Integer lastResult = (firstResult+maxResult) > listRetorno.size() ? listRetorno.size():(firstResult+maxResult);
		listRetorno = listRetorno.subList(firstResult, lastResult);
		//Preenche nroRegConselho
		for (PacientesEmAtendimentoVO linha : listRetorno) {
			linha.setNroRegConselhoResp(getAghuFacade().pesquisarNroRegConselho(linha.getMatriculaResp(), linha.getVinCodigoResp()));
		}
		return listRetorno;
	}

	private List<PacientesEmAtendimentoVO> criarListaPacientesEmAtendimento(
			Integer pacCodigo, String leito, Short unfSeq,
			Integer matriculaResp, Short vinCodigoResp,
			DominioOrigemAtendimento origem, Short espSeq)
			throws ApplicationBusinessException {
		AghParametros parametroDiasAmbulatorio = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_DIAS_ATD_AMBULATORIO);
		Date dataInicio = DateUtil.adicionaDias(new Date(), -parametroDiasAmbulatorio.getVlrNumerico().intValue());
		Date dataFim = new Date();
		List<PacientesEmAtendimentoVO> listRetorno = new ArrayList<PacientesEmAtendimentoVO>();
		listRetorno.addAll(getAghuFacade().listarPacientesEmAtendimentoUnion1(pacCodigo, leito, unfSeq, matriculaResp, vinCodigoResp, origem, espSeq));
		listRetorno.addAll(getAghuFacade().listarPacientesEmAtendimentoUnion2(dataInicio, dataFim, pacCodigo, unfSeq, matriculaResp, vinCodigoResp, origem, espSeq));
		
		return listRetorno;
	}
	
	public Long listarPacientesEmAtendimentoCount(Integer pacCodigo, String leito, Short unfSeq,
			Integer matriculaResp, Short vinCodigoResp,
			DominioOrigemAtendimento origem, Short espSeq) throws ApplicationBusinessException {
		
		List<PacientesEmAtendimentoVO> listRetorno = new ArrayList<PacientesEmAtendimentoVO>();
		listRetorno = criarListaPacientesEmAtendimento(pacCodigo, leito, unfSeq, matriculaResp, vinCodigoResp, origem, espSeq);
		
		return Long.valueOf(listRetorno.size());
	}
}