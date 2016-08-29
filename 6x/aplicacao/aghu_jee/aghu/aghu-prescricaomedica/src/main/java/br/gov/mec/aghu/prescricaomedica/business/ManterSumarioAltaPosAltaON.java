package br.gov.mec.aghu.prescricaomedica.business;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MpmAltaMotivo;
import br.gov.mec.aghu.model.MpmAltaPlano;
import br.gov.mec.aghu.model.MpmAltaPrincReceitas;
import br.gov.mec.aghu.model.MpmAltaPrincReceitasId;
import br.gov.mec.aghu.model.MpmAltaRecomendacao;
import br.gov.mec.aghu.model.MpmAltaRecomendacaoId;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.model.MpmPlanoPosAlta;
import br.gov.mec.aghu.model.MpmServRecomendacaoAlta;
import br.gov.mec.aghu.model.VMpmServRecomAltas;
import br.gov.mec.aghu.model.VMpmServRecomAltasId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaMotivoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPlanoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPrincReceitasDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoAltaMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPlanoPosAltaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmServRecomendacaoAltaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.VMpmServRecomAltasDAO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaCadastradaVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaPrincReceitasVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaPosAltaMotivoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 *
 * @author gfmenezes
 */
@Stateless
public class ManterSumarioAltaPosAltaON extends BaseBusiness {


@EJB
private ManterAltaMotivoRN manterAltaMotivoRN;

@EJB
private ManterAltaPlanoRN manterAltaPlanoRN;

@EJB
private ManterAltaRecomendacaoRN manterAltaRecomendacaoRN;

private static final Log LOG = LogFactory.getLog(ManterSumarioAltaPosAltaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmMotivoAltaMedicaDAO mpmMotivoAltaMedicaDAO;

@Inject
private MpmAltaMotivoDAO mpmAltaMotivoDAO;

@Inject
private MpmAltaPlanoDAO mpmAltaPlanoDAO;

@Inject
private VMpmServRecomAltasDAO vMpmServRecomAltasDAO;

@Inject
private MpmPlanoPosAltaDAO mpmPlanoPosAltaDAO;

@Inject
private MpmServRecomendacaoAltaDAO mpmServRecomendacaoAltaDAO;

@Inject
private MpmAltaPrincReceitasDAO mpmAltaPrincReceitasDAO;

@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@EJB
private IAmbulatorioFacade ambulatorioFacade;


	//private final static String anteciparSumario = "ANTECIPAR SUMARIO";

	/**
	 * 
	 */
	private static final long serialVersionUID = -1365976981138466362L;

	public enum ManterSumarioAltaPosAltaONExceptionCode implements BusinessExceptionCode {
		//alterar conforme for surgindo necessidade
		COMPLEMENTO_OBRIGATORIO,
		SELECIONAR_RECOMENDACAO_ALTA_GRAVAR
		; 

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}

	/**
	 * Busca em MPM_MOTIVO_ALTA_MEDICAS.<br>
	 * Entidade: <code>MpmMotivoAltaMedica</code><br>
	 * Regra retorna todos os itens devido a melhoria pedido pelo analista.
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SumarioAltaPosAltaMotivoVO> listaMotivoAltaMedica(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		List<SumarioAltaPosAltaMotivoVO> listVo = new LinkedList<SumarioAltaPosAltaMotivoVO>();

		SumarioAltaPosAltaMotivoVO vo;
		List<MpmMotivoAltaMedica> list = this.getMotivoAltaMedicaDAO().listMotivoAltaMedica();
		for (MpmMotivoAltaMedica motivoAltaMedica : list) {

				vo = new SumarioAltaPosAltaMotivoVO();
				vo.setDescricao(motivoAltaMedica.getDescricao());
				vo.setExigeComplemento(motivoAltaMedica.getIndExigeComplemento());
				vo.setMotivoAltaMedica(motivoAltaMedica);
				vo.setAltaSumario(altaSumario);

				listVo.add(vo);	
		}
		return listVo;
	}

	/**
	 * Busca em MPM_PLANO_POS_ALTA.<br>
	 * Entidade: <code>MpmPlanoPosAlta</code><br>
	 * Executa a regra pra nao retornar os itens jah associados ao Sumario de Alta.
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SumarioAltaPosAltaMotivoVO> listaPlanoPosAlta(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		List<SumarioAltaPosAltaMotivoVO> listVo = new LinkedList<SumarioAltaPosAltaMotivoVO>();

		List<MpmPlanoPosAlta> itensExistentes = new LinkedList<MpmPlanoPosAlta>();
		if (altaSumario != null) {
			List<MpmAltaPlano> altaPlanoList = this.getMpmAltaPlanoDAO().buscaAltaPlanoPorAltaSumario(altaSumario);
			for (MpmAltaPlano mpmAltaPlano : altaPlanoList) {
				itensExistentes.add(mpmAltaPlano.getMpmPlanoPosAltas());
			}
		}

		SumarioAltaPosAltaMotivoVO vo;
		List<MpmPlanoPosAlta> list = this.getMpmPlanoPosAltaDAO().listaPlanoPosAlta();
		for (MpmPlanoPosAlta planoPosAlta : list) {
			if (!itensExistentes.contains(planoPosAlta)) {
				vo = new SumarioAltaPosAltaMotivoVO();

				vo.setDescricao(planoPosAlta.getDescricao());
				vo.setExigeComplemento(planoPosAlta.getIndExigeComplemento());
				vo.setPlanoPosAlta(planoPosAlta);
				vo.setAltaSumario(altaSumario);

				listVo.add(vo);	
			}
		}

		return listVo;
	}

	/**
	 * Busca as <code>MpmAltaMotivo</code> associadas ao Sumario de Alta.<br>
	 * 
	 * @param altaSumario
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SumarioAltaPosAltaMotivoVO> buscaAltaMotivo(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		if (altaSumario == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}

		List<SumarioAltaPosAltaMotivoVO> listVo = new LinkedList<SumarioAltaPosAltaMotivoVO>();

		SumarioAltaPosAltaMotivoVO vo;
		List<MpmAltaMotivo> list = this.getMpmAltaMotivoDAO().buscaAltaMotivoPorAltaSumario(altaSumario);
		for (MpmAltaMotivo altaMotivo : list) {
			vo = new SumarioAltaPosAltaMotivoVO();

			vo.setDescricao(
					(altaMotivo.getComplMotivo() == null 
							? altaMotivo.getMotivoAltaMedicas().getDescricao()
									: altaMotivo.getMotivoAltaMedicas().getDescricao() + "; " + altaMotivo.getComplMotivo()));
			if(altaMotivo.getMotivoAltaMedicas().getIndExigeComplemento()) {
				vo.setComplemento(altaMotivo.getComplMotivo());
			}
			vo.setExigeComplemento(altaMotivo.getMotivoAltaMedicas().getIndExigeComplemento());
			vo.setMotivoAltaMedica(altaMotivo.getMotivoAltaMedicas());
			vo.setAltaSumario(altaSumario);
			vo.setIdItem(altaMotivo.getId());

			listVo.add(vo);
		}

		return listVo;
	}

	/**
	 * Busca as <code>MpmAltaPlano</code> associadas ao Sumario de Alta.<br>
	 * 
	 * @param altaSumario
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SumarioAltaPosAltaMotivoVO> buscaAltaPlano(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		if (altaSumario == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}

		List<SumarioAltaPosAltaMotivoVO> listVo = new LinkedList<SumarioAltaPosAltaMotivoVO>();

		SumarioAltaPosAltaMotivoVO vo;
		List<MpmAltaPlano> list = this.getMpmAltaPlanoDAO().buscaAltaPlanoPorAltaSumario(altaSumario);
		for (MpmAltaPlano altaPlano : list) {
			vo = new SumarioAltaPosAltaMotivoVO();

			String descricao = StringUtil.concatenar(altaPlano.getMpmPlanoPosAltas().getDescricao(), altaPlano.getComplPlanoPosAlta(), "; ");
			vo.setDescricao(descricao);
			vo.setComplemento(altaPlano.getComplPlanoPosAlta());
			vo.setExigeComplemento(altaPlano.getMpmPlanoPosAltas().getIndExigeComplemento());
			vo.setPlanoPosAlta(altaPlano.getMpmPlanoPosAltas());
			vo.setAltaSumario(altaSumario);
			vo.setIdItem(altaPlano.getId());

			listVo.add(vo);
		}

		return listVo;
	}

	/**
	 * Grava ou altera as informações 
	 * de <code> MpmAltaMotivo </code> 
	 *
	 * @param altaMotivo
	 * @throws ApplicationBusinessException
	 */
	public void gravarAltaMotivo(MpmAltaMotivo altaMotivo) throws ApplicationBusinessException {
		if(altaMotivo.getId() == null) {
			this.getManterAltaMotivoRN().inserirAltaMotivo(altaMotivo);
		} else {
			MpmAltaMotivo mpmAltaMotivo = this.getManterAltaMotivoRN().getAltaMotivoDAO()
				.obterMpmAltaMotivo(altaMotivo.getId().getApaAtdSeq(), altaMotivo.getId().getApaSeq(),altaMotivo.getId().getSeqp(), Boolean.FALSE);
			mpmAltaMotivo.setComplMotivo(altaMotivo.getComplMotivo());
			this.getManterAltaMotivoRN().atualizarAltaMotivo(mpmAltaMotivo);
		}
	}

	/**
	 * Exclui um <code> MpmAltaMotivo </code>
	 * do banco.
	 * 
	 * @param vo
	 */
	public void removerAltaMotivo(MpmAltaMotivo altaMotivo) throws ApplicationBusinessException {
		if (altaMotivo == null || altaMotivo.getId() == null) {
			throw new IllegalArgumentException("Parametro Invalido!!");
		}
		MpmAltaMotivo mpmAltaMotivo = this.getMpmAltaMotivoDAO().obterMpmAltaMotivo(altaMotivo.getId().getApaAtdSeq(), altaMotivo.getId().getApaSeq(),altaMotivo.getId().getSeqp(), Boolean.FALSE);
		mpmAltaMotivo.getAltaSumario().setAltaMotivos(null);
		this.getManterAltaMotivoRN().removerAltaMotivo(mpmAltaMotivo);
	}

	public void gravarAltaPlano(MpmAltaPlano umMpmAltaPlano) throws ApplicationBusinessException {
		if (umMpmAltaPlano != null && umMpmAltaPlano.getMpmPlanoPosAltas() != null
				&& umMpmAltaPlano.getMpmPlanoPosAltas().getIndExigeComplemento()
				&& StringUtils.isBlank(umMpmAltaPlano.getComplPlanoPosAlta())) {
			throw new ApplicationBusinessException(ManterSumarioAltaPosAltaONExceptionCode.COMPLEMENTO_OBRIGATORIO);
		}

		if(umMpmAltaPlano.getId() == null) {
			this.getManterAltaPlanoRN().inserirAltaPlano(umMpmAltaPlano);
		} else {
			MpmAltaPlano dbAltaPlano = this.getMpmAltaPlanoDAO().obterMpmAltaPlano(umMpmAltaPlano.getId().getApaAtdSeq(), umMpmAltaPlano.getId().getApaSeq(), umMpmAltaPlano.getId().getSeqp());
			dbAltaPlano.setComplPlanoPosAlta(umMpmAltaPlano.getComplPlanoPosAlta());
			this.getManterAltaPlanoRN().atualizarAltaPlano(dbAltaPlano);
		}
	}

	public void removerAltaPlano(MpmAltaPlano umMpmAltaPlano) throws ApplicationBusinessException {
		if (umMpmAltaPlano == null || umMpmAltaPlano.getId() == null) {
			throw new IllegalArgumentException("Parametro Invalido!!");
		}
		MpmAltaPlano altaPlano = this.getMpmAltaPlanoDAO().obterMpmAltaPlanoAtivoInativo(umMpmAltaPlano.getId().getApaAtdSeq(), umMpmAltaPlano.getId().getApaSeq(), umMpmAltaPlano.getId().getSeqp());
		altaPlano.getAltaSumario().setAltaPlanos(null);
		this.getManterAltaPlanoRN().removerPlanoPosAlta(altaPlano);
	}
	
	public void gravarMpmAlta(List<AltaPrincReceitasVO> listaAltaPrincReceitasAux1,List<AltaPrincReceitasVO> listaAltaPrincReceitasAux2, MpmAltaSumario altoSumario) throws ApplicationBusinessException{
		MpmAltaPrincReceitas mpmAltaPrincReceitas = new MpmAltaPrincReceitas();
		if (!listaAltaPrincReceitasAux1.isEmpty()) {
			for (Object prescritos : listaAltaPrincReceitasAux1) {
				List<AltaPrincReceitasVO> altaPrincReceitasVO = mpmAltaPrincReceitasDAO.obterMedicamentosPrescritosAlta(prescritos.toString(), altoSumario);
				if (!altaPrincReceitasVO.isEmpty()) {
					for(AltaPrincReceitasVO mpmPrinc : altaPrincReceitasVO){
						
						MpmAltaPrincReceitasId id = new MpmAltaPrincReceitasId(mpmPrinc.getApaAtdSeq(),mpmPrinc.getApaSeq(),mpmPrinc.getAsuSeqp(),mpmPrinc.getSeqp());

						if (mpmPrinc.getIndCarga().equalsIgnoreCase("N")) {
							mpmPrinc.setIndCarga("S");
							mpmAltaPrincReceitas = mpmAltaPrincReceitasDAO.obterPorChavePrimaria(id);
							mpmAltaPrincReceitas.setIndCarga(mpmPrinc.getIndCarga());
							
							mpmAltaPrincReceitasDAO.atualizar(mpmAltaPrincReceitas);
							mpmAltaPrincReceitasDAO.flush();
						}
					}
				}
			}
		}
		if(!listaAltaPrincReceitasAux2.isEmpty()){
			for (Object excluidos : listaAltaPrincReceitasAux2) {
				List<AltaPrincReceitasVO> altaPrincReceitasVO = mpmAltaPrincReceitasDAO.obterMedicamentosPrescritosAlta(excluidos.toString(), altoSumario);
				if (!altaPrincReceitasVO.isEmpty()) {
					for(AltaPrincReceitasVO mpmPrinc2 : altaPrincReceitasVO){
						MpmAltaPrincReceitasId id = new MpmAltaPrincReceitasId(mpmPrinc2.getApaAtdSeq(),mpmPrinc2.getApaSeq(),mpmPrinc2.getAsuSeqp(),mpmPrinc2.getSeqp());
						if (mpmPrinc2.getIndCarga().equalsIgnoreCase("S")) {
							mpmPrinc2.setIndCarga("N");
							mpmAltaPrincReceitas = mpmAltaPrincReceitasDAO.obterPorChavePrimaria(id);
							mpmAltaPrincReceitas.setIndCarga(mpmPrinc2.getIndCarga());
							
							mpmAltaPrincReceitasDAO.atualizar(mpmAltaPrincReceitas);
							mpmAltaPrincReceitasDAO.flush();
						}
					}
				}
			}
		}
	}
	

	public void validaListaMpmAlta(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp, Integer atdSeq) {
		List<MamReceituarios> geral = prescricaoMedicaFacade.buscarDadosReceituario(Integer.valueOf(atdSeq), DominioTipoReceituario.G);
		List<MamReceituarios> especial = prescricaoMedicaFacade.buscarDadosReceituario(Integer.valueOf(atdSeq), DominioTipoReceituario.E);
		List<MamItemReceituario> itemReceitaGeralList = null;
		List<MamItemReceituario> itemReceitaEspecialList = null;
		if(!geral.isEmpty()){
			itemReceitaGeralList = ambulatorioFacade.buscarItensReceita(geral.get(0));
		}
		if(!especial.isEmpty()){
			itemReceitaEspecialList = ambulatorioFacade.buscarItensReceita(especial.get(0));
		}

		//delete todos os itens do cadastro pela a chave primaria
		mpmAltaPrincReceitasDAO.deleteEmLoteByID(asuApaAtdSeq, asuApaSeq , asuSeqp);
		
		Short seqp = 1;
		if(itemReceitaGeralList != null){
			for (MamItemReceituario itemReceita : itemReceitaGeralList) {
				seqp = cadastraAltaPrincipalReceita(itemReceita, asuApaAtdSeq, asuApaSeq, asuSeqp, seqp);
			}
		}
		if(itemReceitaEspecialList != null){
			for (MamItemReceituario itemReceita : itemReceitaEspecialList) {
				seqp = cadastraAltaPrincipalReceita(itemReceita, asuApaAtdSeq, asuApaSeq, asuSeqp, seqp);
			}
		}
	}
	
	private Short cadastraAltaPrincipalReceita(MamItemReceituario itemReceita, Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp, Short seqp ){
		AltaPrincReceitasVO vo =  criaAltaPrincReceitasVO(itemReceita);
		MpmAltaPrincReceitasId id = new MpmAltaPrincReceitasId(asuApaAtdSeq, asuApaSeq , asuSeqp, seqp);
		MpmAltaPrincReceitas mpmAltaPrincReceitas = mpmAltaPrincReceitasDAO.obterPorChavePrimaria(id);
		
	    if(mpmAltaPrincReceitas == null) {
			mpmAltaPrincReceitas = new MpmAltaPrincReceitas();
			mpmAltaPrincReceitas.setId(id);
			mpmAltaPrincReceitas.setDescReceita(vo.getReceita());
			mpmAltaPrincReceitas.setIndSituacao(vo.getIndSituacao().toString());
			mpmAltaPrincReceitas.setIndCarga("S");
			mpmAltaPrincReceitasDAO.persistir(mpmAltaPrincReceitas);
			mpmAltaPrincReceitasDAO.flush();
			seqp++;
	    }
	    return seqp;
	}
	
	private AltaPrincReceitasVO criaAltaPrincReceitasVO (MamItemReceituario itemReceita){
		AltaPrincReceitasVO vo = new AltaPrincReceitasVO();
		vo.setDescricao(itemReceita.getDescricao());
		vo.setQuantidade(itemReceita.getQuantidade());
		vo.setIndInterno(itemReceita.getIndInterno());
		vo.setIndUsoContinuo(itemReceita.getIndUsoContinuo());
		vo.setIndSituacao(itemReceita.getIndSituacao());
		vo.setSeqp(itemReceita.getId().getSeqp());
		return vo;
	}

	private MpmAltaPlanoDAO getMpmAltaPlanoDAO() {
		return mpmAltaPlanoDAO;
	}

	protected MpmMotivoAltaMedicaDAO getMotivoAltaMedicaDAO() {
		return mpmMotivoAltaMedicaDAO;
	}

	private MpmPlanoPosAltaDAO getMpmPlanoPosAltaDAO() {
		return mpmPlanoPosAltaDAO;
	}

	private MpmAltaMotivoDAO getMpmAltaMotivoDAO() {
		return mpmAltaMotivoDAO;
	}

	private ManterAltaMotivoRN getManterAltaMotivoRN() {
		return manterAltaMotivoRN;
	}

	private ManterAltaPlanoRN getManterAltaPlanoRN() {
		return manterAltaPlanoRN;
	}

	private VMpmServRecomAltasDAO getVMpmServRecomAltasDAO() {
		return vMpmServRecomAltasDAO;
	}
	
	public MpmAltaPrincReceitasDAO getMpmAltaPrincReceitasDAO() {
		return mpmAltaPrincReceitasDAO;
	}

	public void setMpmAltaPrincReceitasDAO(
			MpmAltaPrincReceitasDAO mpmAltaPrincReceitasDAO) {
		this.mpmAltaPrincReceitasDAO = mpmAltaPrincReceitasDAO;
	}

	public List<AltaCadastradaVO> listarAltaCadastrada(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		if (altaSumario == null) {
			throw new IllegalArgumentException("Parametro Invalido!");
		}

		List<VMpmServRecomAltas> list = this.getVMpmServRecomAltasDAO().listarVMpmServRecomAltas(altaSumario);

		// Preenche o VO que será listado na tela
		List<AltaCadastradaVO> listVo = new LinkedList<AltaCadastradaVO>();
		AltaCadastradaVO vo;
		for (VMpmServRecomAltas altaCadastrada : list) {
			vo = new AltaCadastradaVO();
			vo.setDescricao(altaCadastrada.getDescricao());
			vo.setId(altaCadastrada.getId());
			vo.setIndRecomendado(altaCadastrada.getIndRecomendado());
			vo.setIndSituacao(altaCadastrada.getIndSituacao());
			vo.setIndTipoSumrAlta(altaCadastrada.getIndTipoSumrAlta());

			listVo.add(vo);
		}
		return listVo;
	}

	public List<AltaCadastradaVO> listarAltaCadastradaGravada(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		if (altaSumario == null) {
			throw new IllegalArgumentException("Parametro Invalido!");
		}
		// Recupera lista das Alta Recomendaçoes que estão no banco
		List<MpmAltaRecomendacao> listaAltaRecomendacoes = this.getManterAltaRecomendacaoRN().obterMpmAltaRecomendacao(
				altaSumario.getId().getApaAtdSeq(),
				altaSumario.getId().getApaSeq(),
				altaSumario.getId().getSeqp());

		// Preenche o VO que será listado na tela
		List<AltaCadastradaVO> listVo = new LinkedList<AltaCadastradaVO>();
		AltaCadastradaVO vo;
		for (MpmAltaRecomendacao altaCadastrada : listaAltaRecomendacoes) {
			vo = new AltaCadastradaVO();
			if (altaCadastrada != null && altaCadastrada.getServidorRecomendacaoAlta() != null) {
				vo.setDescricao(altaCadastrada.getDescricao());
				vo.setIndSituacao(altaCadastrada.getIndSituacao());
				// Valor padrao.
				vo.setIndRecomendado(Boolean.FALSE);
				vo.setAltaRecomendacaoId(altaCadastrada.getId());
				vo.setAltaSumario(altaSumario);

				if (altaCadastrada.getServidorRecomendacaoAlta() != null) {
					vo.setIndTipoSumrAlta(altaCadastrada.getServidorRecomendacaoAlta().getIndTipoSumrAlta());
					if (altaCadastrada.getServidorRecomendacaoAlta().getId() != null 
							&& altaCadastrada.getServidorRecomendacaoAlta().getServidor() != null
							&& altaCadastrada.getServidorRecomendacaoAlta().getServidor().getId() != null) {
						VMpmServRecomAltasId id = new VMpmServRecomAltasId();
						id.setSerVinCodigo(altaCadastrada.getServidorRecomendacaoAlta().getServidor().getId().getVinCodigo());
						id.setSerMatricula(altaCadastrada.getServidorRecomendacaoAlta().getServidor().getId().getMatricula());
						id.setSeqp(altaCadastrada.getServidorRecomendacaoAlta().getId().getSeqp());
						vo.setId(id);
					}
				}
				listVo.add(vo);
			}
		}
		return listVo;
	}

	public void gravarAltasCadastradasSelecionadas(
			List<AltaCadastradaVO> listaAltasCadastradas,
			MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		if (listaAltasCadastradas == null) {
			throw new IllegalArgumentException(
			"Parametro obrigatorio nao informado!!!");
		}

		// Insere AltaRecomendacao para cada itens marcados na tela.
		inserirAltaRecomendacaoCadastrada(listaAltasCadastradas, altaSumario);
	}

	private void inserirAltaRecomendacaoCadastrada(List<AltaCadastradaVO> listaAltasCadastradas, MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		boolean gravou = false;
		MpmServRecomendacaoAlta mpmServRecomendacaoAlta;
		MpmAltaRecomendacao altaRecomendacao;
		for (AltaCadastradaVO altaCadastradaVO : listaAltasCadastradas) {

			if (altaCadastradaVO.getGravar() != null && altaCadastradaVO.getGravar()) {
				altaRecomendacao = new MpmAltaRecomendacao();

				altaRecomendacao.setDescricao(altaCadastradaVO.getDescricao());
				altaRecomendacao.setDescricaoSistema(altaCadastradaVO.getDescricao());
				altaRecomendacao.setAltaSumario(altaSumario);

				mpmServRecomendacaoAlta = this.getMpmServRecomendacaoAltaDAO().obter(
						altaCadastradaVO.getId().getSeqp(),
						altaCadastradaVO.getId().getSerMatricula(),
						altaCadastradaVO.getId().getSerVinCodigo());

				altaRecomendacao.setServidorRecomendacaoAlta(mpmServRecomendacaoAlta);
				altaRecomendacao.setIndSituacao(DominioSituacao.A);

				this.getManterAltaRecomendacaoRN().inserirAltaRecomendacao(altaRecomendacao);

				gravou = true;
			}// IF

		}// FOR
		if (!gravou) {
			ManterSumarioAltaPosAltaONExceptionCode.SELECIONAR_RECOMENDACAO_ALTA_GRAVAR.throwException();
		}
	}

	public void inativarAltaRecomendacaoCadastrada(MpmAltaSumario altaSumario, MpmAltaRecomendacaoId altaRecomendacaoId) throws ApplicationBusinessException {
		List<MpmAltaRecomendacao> listaAltaRecomendacoes = this.getManterAltaRecomendacaoRN().obterMpmAltaRecomendacao(
				altaSumario.getId().getApaAtdSeq(),
				altaSumario.getId().getApaSeq(),
				altaSumario.getId().getSeqp());

		if (listaAltaRecomendacoes != null) {
			for (MpmAltaRecomendacao altaRemomendacao : listaAltaRecomendacoes) {
				// Inativa apenas as Alta Cadastradas selecionadas.
				if (altaRecomendacaoId.getAsuApaAtdSeq().equals(altaRemomendacao.getId().getAsuApaAtdSeq()) &&
						altaRecomendacaoId.getAsuApaSeq().equals(altaRemomendacao.getId().getAsuApaSeq()) &&
						altaRecomendacaoId.getAsuSeqp().equals(altaRemomendacao.getId().getAsuSeqp()) &&
						altaRecomendacaoId.getSeqp().equals(altaRemomendacao.getId().getSeqp())) {
					altaRemomendacao.setIndSituacao(DominioSituacao.I);
					this.getManterAltaRecomendacaoRN().atualizarAltaRecomendacao(altaRemomendacao);
					break;
				}
			}
		}
	}
	
	private ManterAltaRecomendacaoRN getManterAltaRecomendacaoRN() {
		return manterAltaRecomendacaoRN;
	}

	private MpmServRecomendacaoAltaDAO getMpmServRecomendacaoAltaDAO() {
		return mpmServRecomendacaoAltaDAO;
	}

}