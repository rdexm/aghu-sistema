package br.gov.mec.aghu.configuracao.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.vo.CidVO;
import br.gov.mec.aghu.configuracao.vo.EquipeVO;
import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.configuracao.vo.EspecialidadeFiltro;
import br.gov.mec.aghu.configuracao.vo.OrigemPacAtendimentoVO;
import br.gov.mec.aghu.configuracao.vo.PacAtendimentoVO;
import br.gov.mec.aghu.configuracao.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.vo.DadosPacientesEmAtendimentoVO;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class ConfiguracaoServiceImpl implements IConfiguracaoService {

	private static final String PARAMETRO_OBRIGATORIO = "Parâmetro Obrigatório";
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Override
	public List<Especialidade> pesquisarEspecialidade(EspecialidadeFiltro filtro) throws ServiceException {
		String strPesquisa = null;

		if (filtro.getSeq() != null) {
			strPesquisa = filtro.getSeq().toString();
		} else {			
			strPesquisa = filtro.getNomeEspecialidade();
		}
		
		List<AghEspecialidades> listaEspecialidades = this.aghuFacade.pesquisarEspecialidadesPorNomeOuSigla(strPesquisa);
		
		return montarVOEspecialidade(listaEspecialidades);
	}
	
	@Override
	public List<Especialidade> pesquisarEspePorNomeSiglaListaSeq(List<Short> listEspId, EspecialidadeFiltro filtro) throws ServiceException {
		String strPesquisa = null;

		if (filtro.getSeq() != null) {
			strPesquisa = filtro.getSeq().toString();
		} else {			
			strPesquisa = filtro.getNomeEspecialidade();
		}
		
		List<AghEspecialidades> listaEspecialidades = this.aghuFacade.pesquisarEspecPorNomeSiglaListaSeq(listEspId, strPesquisa);				
		
		return montarVOEspecialidade(listaEspecialidades);
	}
	
	@Override 
	public Long pesquisarEspePorNomeSiglaListaSeqCount(List<Short> listEspId, EspecialidadeFiltro filtro) {
		String strPesquisa = null;
		if (filtro.getSeq() != null) {
			strPesquisa = filtro.getSeq().toString();
		} else {			
			strPesquisa = filtro.getNomeEspecialidade();
		}
		
		return this.aghuFacade.pesquisarEspecPorNomeSiglaListaSeqCount(listEspId, strPesquisa);
	}
	
	/**
	 * Monta o VO de especialidades através da AghEspecialidades
	 * 
	 * @param listaEspecialidades
	 * @return
	 */
	private List<Especialidade>  montarVOEspecialidade(List<AghEspecialidades> listaEspecialidades) {
		List<Especialidade> result = new ArrayList<Especialidade>();
		for (AghEspecialidades especialidadeItem : listaEspecialidades) {
			Especialidade especialidade = new Especialidade();
			especialidade.setSeq(especialidadeItem.getSeq());
			especialidade.setSigla(especialidadeItem.getSigla());
			especialidade.setNomeEspecialidade(especialidadeItem.getNomeEspecialidade());
			result.add(especialidade);
		}
		return result;
	}
	
	/**
	 * Web Service #34401
	 */
	@Override
	public Boolean verificarAtendimentoVigentePorCodigo(final Integer codigo) throws ServiceException {
		return this.aghuFacade.verificarAtendimentoVigentePorCodigo(codigo);
	}

	@Override
	public List<Especialidade> pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(String param, Integer maxResults) throws ServiceException {
		List<AghEspecialidades> listaEspecialidades = this.aghuFacade.pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(param, maxResults);
		return montarVOEspecialidade(listaEspecialidades);
	}
	
	@Override
	public List<Especialidade> pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(String param) throws ServiceException {
		List<AghEspecialidades> listaEspecialidades = this.aghuFacade.pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(param, null);
		return montarVOEspecialidade(listaEspecialidades);
	}
	
	@Override
	public Long pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount(String param) throws ServiceException {
		return this.aghuFacade.pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount(param);
	}
	
	@Override
	public List<Especialidade> pesquisarEspecialidadesAtivasPorSeqs(List<Short> listSeqs) throws ServiceException {
		if (listSeqs == null || listSeqs.isEmpty()) {
			return new ArrayList<Especialidade>();
		}
		List<AghEspecialidades> listaEspecialidades = this.aghuFacade.pesquisarEspecialidadesAtivasPorSeqs(listSeqs);
		return montarVOEspecialidade(listaEspecialidades);
	}
	
	@Override
	public List<Especialidade> pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqs(String param, List<Short> listSeqs) throws ServiceException {
		if (listSeqs == null || listSeqs.isEmpty()) {
			return new ArrayList<Especialidade>();
		}
		List<AghEspecialidades> listaEspecialidades = this.aghuFacade.pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqs(param, listSeqs);
		return montarVOEspecialidade(listaEspecialidades);
	}
	
	@Override
	public Long pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqsCount(String param, List<Short> listSeqs) throws ServiceException {
		if (listSeqs == null || listSeqs.isEmpty()) {
			return 0l;
		}
		return this.aghuFacade.pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqsCount(param, listSeqs);
	}
	
	@Override
	public List<Especialidade> pesquisarEspecialidadesAtivasPorSeqs(List<Short> listSeqs,
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ServiceException {
		if (listSeqs == null || listSeqs.isEmpty()) {
			return new ArrayList<Especialidade>();
		}
		List<AghEspecialidades> listaEspecialidades = this.aghuFacade.pesquisarEspecialidadesAtivasPorSeqs(listSeqs, firstResult, maxResults, orderProperty, asc);
		return montarVOEspecialidade(listaEspecialidades);
	}
	
	@Override
	public Long pesquisarEspecialidadesAtivasPorSeqsCount(List<Short> listSeqs) throws ServiceException {
		if (listSeqs == null || listSeqs.isEmpty()) {
			return 0l;
		}
		return this.aghuFacade.pesquisarEspecialidadesAtivasPorSeqsCount(listSeqs);
	}
	
	/**
	 * Retornar os Cids pesquisados por codigo e/ou descricao
	 * 
	 * Web Service #36117
	 * 
	 * @param param
	 * @return
	 * @throws ServiceException
	 */
	
	@Override
	public List<CidVO> pesquisarCidPorCodigoDescricao(String param) throws ServiceException {
		
		if (param == null) {
			throw new ServiceException("Parametro esta nulo");
		}
		try {
			List<AghCid> listaCids = this.aghuFacade.pesquisarCidPorCodigoDescricao(param);
			List<CidVO> result = new ArrayList<CidVO>();
			for (AghCid cid : listaCids) {
				CidVO vo = new CidVO();
				vo.setSeq(cid.getSeq());
				vo.setDescricao(cid.getDescricao());
				vo.setCodigo(cid.getCodigo());
				result.add(vo);
			}
			
			return result;
		} catch (RuntimeException e) {
			throw new ServiceException("Erro ao realizar a pesquisa de CIDs por Param : " + e.getMessage());
		}
	

	}
	
	/**
	 * Retornar os Cids pesquisados por seq
	 * 
	 * Web Service #36118
	 * 
	 * @param listSeq
	 * @return
	 * @throws ServiceException
	 */
	
	@Override
	public List<CidVO> pesquisarCidPorSeq(List<Integer> listSeq) throws ServiceException {
		if (listSeq == null || listSeq.isEmpty()) {
			throw new ServiceException("Lista de Seq esta nula/vazia");
		}
		try {
			List<AghCid> listaCids = this.aghuFacade.pesquisarCidPorSeq(listSeq);
			List<CidVO> result = new ArrayList<CidVO>();
			for (AghCid cid : listaCids) {
				CidVO vo = new CidVO();
				vo.setSeq(cid.getSeq());
				vo.setDescricao(cid.getDescricao());
				vo.setCodigo(cid.getCodigo());
				result.add(vo);
			}
			return result;
		} catch (RuntimeException e) {
			throw new ServiceException("Erro ao realizar a pesquisa de CIDs por Seq: " + e.getMessage());
		}
	}
	
	@Override
	public Long pesquisarCidPorCodigoDescricaoCount(String param) throws ServiceException {
		return this.aghuFacade.pesquisarCidPorCodigoDescricaoCount(param);
	}
	
	@Override
	public String pesquisarNomeEspecialidadePorSeq(Short seq) throws ServiceException {
		return this.aghuFacade.pesquisarNomeEspecialidadePorSeq(seq);
	}

	@Override
	public Especialidade obterEspecialidadePorSeq(Short espSeq) throws ServiceException {
		
		AghEspecialidades especialidade = this.aghuFacade.obterEspecialidadePorChavePrimaria(espSeq);
		
		Especialidade result = new Especialidade();
		result.setSeq(especialidade.getSeq());
		result.setSigla(especialidade.getSigla());
		result.setNomeEspecialidade(especialidade.getNomeEspecialidade());
		
		return result;
	}

	@Override
	public List<EquipeVO> pesquisarEquipesAtivasDoCO() throws ServiceException {
		try {
			List<EquipeVO> result = new ArrayList<EquipeVO>();
			List<AghEquipes> aghEquipes = this.aghuFacade.pesquisarEquipesAtivasDoCO();
			if (aghEquipes != null && !aghEquipes.isEmpty()) {
				for (AghEquipes aghEquipe : aghEquipes) {
					EquipeVO equipeVO = new EquipeVO();
					equipeVO.setSeq(aghEquipe.getSeq());
					equipeVO.setNome(aghEquipe.getNome());
					equipeVO.setSerMatricula(aghEquipe.getProfissionalResponsavel().getId().getMatricula());
					equipeVO.setSerVinCodigo(aghEquipe.getProfissionalResponsavel().getId().getVinCodigo());
					equipeVO.setIndPlacarCo(aghEquipe.getIndPlacarCo() != null ? aghEquipe.getIndPlacarCo().isSim() : null);
					equipeVO.setIndAtivo(aghEquipe.getIndSituacao() != null ? aghEquipe.getIndSituacao().isAtivo() : null);
					result.add(equipeVO);
				}
			}
			return result;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<EquipeVO> pesquisarEquipesPorMatriculaVinculo(Integer matricula, Short vinCodigo) throws ServiceException {
		if (matricula == null || vinCodigo == null) {
			throw new ServiceException("Parâmetros obrigatórios");
		}
		try {
			List<EquipeVO> result = new ArrayList<EquipeVO>();
			List<AghEquipes> aghEquipes = this.aghuFacade.pesquisarEquipesPorMatriculaVinculo(matricula, vinCodigo);
			if (aghEquipes != null && !aghEquipes.isEmpty()) {
				for (AghEquipes aghEquipe : aghEquipes) {
					EquipeVO equipeVO = new EquipeVO();
					equipeVO.setSeq(aghEquipe.getSeq());
					equipeVO.setNome(aghEquipe.getNome());
					equipeVO.setSerMatricula(aghEquipe.getProfissionalResponsavel().getId().getMatricula());
					equipeVO.setSerVinCodigo(aghEquipe.getProfissionalResponsavel().getId().getVinCodigo());
					result.add(equipeVO);
				}
			}
			return result;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<EquipeVO> pesquisarEquipesAtivas(String parametro) throws ServiceException {
		try {
			List<EquipeVO> result = new ArrayList<EquipeVO>();
			List<AghEquipes> aghEquipes = this.aghuFacade.getPesquisaEquipesAtivas(parametro);
			if (aghEquipes != null && !aghEquipes.isEmpty()) {
				for (AghEquipes aghEquipe : aghEquipes) {
					EquipeVO equipeVO = new EquipeVO();
					equipeVO.setSeq(aghEquipe.getSeq());
					equipeVO.setNome(aghEquipe.getNome());
					equipeVO.setSerMatricula(aghEquipe.getProfissionalResponsavel().getId().getMatricula());
					equipeVO.setSerVinCodigo(aghEquipe.getProfissionalResponsavel().getId().getVinCodigo());
					result.add(equipeVO);
				}
			}
			return result;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<EquipeVO> pesquisarEquipeAtivaCO(final String parametro) throws ServiceException {
		try {
			List<EquipeVO> result = new ArrayList<EquipeVO>();
			List<AghEquipes> aghEquipes = this.aghuFacade.pesquisarEquipeAtivaCO(parametro);
			if (aghEquipes != null && !aghEquipes.isEmpty()) {
				for (AghEquipes aghEquipe : aghEquipes) {
					EquipeVO equipeVO = new EquipeVO();
					equipeVO.setSeq(aghEquipe.getSeq());
					equipeVO.setNome(aghEquipe.getNome());
					equipeVO.setSerMatricula(aghEquipe.getProfissionalResponsavel().getId().getMatricula());
					equipeVO.setSerVinCodigo(aghEquipe.getProfissionalResponsavel().getId().getVinCodigo());
					result.add(equipeVO);
				}
			}
			return result;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public Long pesquisarEquipeAtivaCOCount(final String parametro) throws ServiceException {
		return this.aghuFacade.pesquisarEquipeAtivaCOCount(parametro);
	}

	@Override
	public List<UnidadeFuncionalVO> pesquisarAghUnidadesFuncionaisAtivasPorSeqOuDescricao(String parametro) throws ServiceException {
		try {
			List<UnidadeFuncionalVO> result = new ArrayList<UnidadeFuncionalVO>();
			List<AghUnidadesFuncionais> unidades = this.aghuFacade.pesquisarAghUnidadesFuncionaisAtivasPorSeqOuDescricao(parametro, 0, 100,
					AghUnidadesFuncionais.Fields.DESCRICAO.toString(), true);
			if (unidades != null && !unidades.isEmpty()) {
				for (AghUnidadesFuncionais aghUnidadeFuncional : unidades) {
					result.add(new UnidadeFuncionalVO(aghUnidadeFuncional.getSeq(), aghUnidadeFuncional.getDescricao()));
				}
			}
			return result;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Long pesquisarAghUnidadesFuncionaisAtivasPorSeqOuDescricaoCount(String parametro) throws ServiceException {
		try {
			return this.aghuFacade.pesquisarAghUnidadesFuncionaisAtivasPorSeqOuDescricaoCount(parametro);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<CidVO> pesquisarCidAtivosPorSeq(List<Integer> listSeq) throws ServiceException {
		if (listSeq == null || listSeq.isEmpty()) {
			throw new ServiceException(PARAMETRO_OBRIGATORIO);
		}
		List<AghCid> listaCids = this.aghuFacade.pesquisarCidAtivosPorSeq(listSeq);
		List<CidVO> result = new ArrayList<CidVO>();
		for (AghCid cid : listaCids) {
			CidVO vo = new CidVO();
			vo.setSeq(cid.getSeq());
			vo.setDescricao(cid.getDescricao());
			vo.setCodigo(cid.getCodigo());
			result.add(vo);
		}
		return result;
	}
	
	@Override
	public PacAtendimentoVO obterDadosPacientesEmAtendimento(Integer conNumero) throws ServiceException {
		PacAtendimentoVO result = new PacAtendimentoVO();
		try {
			DadosPacientesEmAtendimentoVO dadosVO = this.aghuFacade.obterDadosPacientesEmAtendimento(conNumero);
			
			if (dadosVO != null) {
				result.setDtConsulta(dadosVO.getDtConsulta());
				result.setAtdSeq(dadosVO.getAtdSeq());
				result.setAtdSerMatricula(dadosVO.getAtdSerMatricula());
				result.setAtdSerVinCodigo(dadosVO.getAtdSerVinCodigo());
				result.setAtdUnfSeq(dadosVO.getAtdUnfSeq());
				result.setEspSeq(dadosVO.getEspSeq());
				result.setEspSigla(dadosVO.getEspSigla());
			}
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
		return result;
	}
	
	/**
	 * #34722 - Consulta utilizada para verificar se determinada unidade funcional possui determinada característica associada
	 * @param unfSeq
	 * @param caracteristica
	 * @return
	 */
	
	@Override
	public Integer buscarSeqAtendimentoPorConNumero(Integer conNumero) throws ServiceException {
		try {
			AghAtendimentos atendimento = this.aghuFacade.buscarAtendimentoPorConNumero(conNumero);
			if (atendimento != null) {
				return atendimento.getSeq();
			}
			return null;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	
	@Override
	public List<Short> pesquisarUnidFuncExecutora(){
		return this.aghuFacade.pesquisarUnidFuncExecutora();
	}
	
	@Override
	public Date obterDataInicioAtendimentoPorPaciente(Integer pacCodigo, Date dthrInicio) throws ServiceException {
		try {
			AghAtendimentos atendimento = this.aghuFacade.obterAtendimentoPorPacienteDataInicio(pacCodigo, dthrInicio);
			if (atendimento != null) {
				return atendimento.getDthrInicio();
			}
			return null;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public void atualizarAtendimentoDthrNascimento(Integer pacCodigo, Date dthrInicio, Date dthrNascimento) throws ServiceException {
		try {
			this.aghuFacade.atualizarAtendimentoDthrNascimento(pacCodigo, dthrInicio, dthrNascimento);			
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * # 39006 - Serviço que obtem AghAtendimentos
	 * @param seq
	 * @return
	 */
	@Override
	public OrigemPacAtendimentoVO obterAghAtendimentosPorSeq(Integer seq) throws ServiceException{
		try {
			AghAtendimentos atendimento = this.aghuFacade.obterAghAtendimentosPorSeq(seq);
			OrigemPacAtendimentoVO pacAtendimentoVO = new OrigemPacAtendimentoVO();
			if (atendimento != null) {
				pacAtendimentoVO.setIndPacAtendimento(atendimento.getIndPacAtendimento() == null ? null : atendimento.getIndPacAtendimento().getDescricao());
				pacAtendimentoVO.setOrigem(atendimento.getOrigem() == null ? null : atendimento.getOrigem().getDescricao());
				pacAtendimentoVO.setEspSeq(atendimento.getEspecialidade() == null ? null : atendimento.getEspecialidade().getSeq());
			}
			return pacAtendimentoVO;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	
	public Especialidade buscarEspecialidadePorConNumero(Integer conNumero) throws ServiceException {
		AghEspecialidades especialidade = this.aghuFacade.buscarEspecialidadePorConNumero(conNumero);
		try {

			Especialidade vo = new Especialidade();
			if (especialidade != null) {
				vo.setNomeEspecialidade(especialidade.getNomeEspecialidade());
				vo.setSeq(especialidade.getSeq());
				vo.setSigla(especialidade.getSigla());
			}
			return vo;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public PacAtendimentoVO obterAtendimentoPorPacienteDataInicioOrigem(Integer pacCodigo, Date dthrInicio, String ... origemAtendimento)  throws ServiceException {
		
		notNull(pacCodigo, PARAMETRO_OBRIGATORIO);
		notNull(dthrInicio, PARAMETRO_OBRIGATORIO);
		notNull(origemAtendimento, PARAMETRO_OBRIGATORIO);
		
		if(origemAtendimento.length == 0){
			throw new ServiceException(PARAMETRO_OBRIGATORIO);
		}
		
		DominioOrigemAtendimento[] listDominioOrigemAtendimento = new DominioOrigemAtendimento[origemAtendimento.length];
		
		for (int i = 0; i < origemAtendimento.length; i++) {
			if(StringUtils.isNotBlank(origemAtendimento[i])){
				try {
					listDominioOrigemAtendimento[i] = DominioOrigemAtendimento.getInstance(StringUtils.trim(origemAtendimento[i]));
				} catch (RuntimeException e) {
					throw new ServiceException("Origem do Atendimento Inválida ");
				}
			}
		}
		
		PacAtendimentoVO result = new PacAtendimentoVO();
		
		try {
			AghAtendimentos atendimento = aghuFacade.obterAtendimentoPorPacienteDataInicioOrigem(pacCodigo, dthrInicio, listDominioOrigemAtendimento);
			
			if(atendimento != null){
				result.setAtdSeq(atendimento.getSeq());
			}
			
			return result;
			
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	
	}
	
    public static void notNull(Object object, String message) throws ServiceException {
    	try {
    		Validate.notNull(object, message);
		} catch (IllegalArgumentException e) {
			throw new ServiceException(e.getMessage());
		}
    }

}
