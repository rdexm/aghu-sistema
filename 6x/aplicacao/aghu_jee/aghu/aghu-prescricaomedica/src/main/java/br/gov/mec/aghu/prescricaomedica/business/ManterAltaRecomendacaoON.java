package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaRecomendacao;
import br.gov.mec.aghu.model.MpmAltaRecomendacaoId;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaRecomendacaoDAO;
import br.gov.mec.aghu.prescricaomedica.util.PrescricaoMedicaTypes;
import br.gov.mec.aghu.prescricaomedica.vo.AltaCadastradaVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaRecomendacaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterAltaRecomendacaoON extends BaseBusiness {


@EJB
private VerificarPrescricaoON verificarPrescricaoON;

@EJB
private ManterPrescricaoMedicaON manterPrescricaoMedicaON;

@EJB
private ManterAltaRecomendacaoRN manterAltaRecomendacaoRN;

@EJB
private IAghuFacade aghuFacade;

@Inject
private MpmAltaRecomendacaoDAO mpmAltaRecomendacaoDAO;

private static final Log LOG = LogFactory.getLog(ManterAltaRecomendacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8096979633223672397L;

	public enum ManterAltaRecomendacaoONExceptionCode implements BusinessExceptionCode {
		/**
		 * Campo Descrição é obrigatório
		 */
		DESCRICAO_OBRIGATORIA, SELECIONAR_RECOMENDACAO_ALTA_GRAVAR, ALTA_RECOMENDACAO_INALTERADA;
	}
	
	
	/**
	 * Atualiza alta recomendação do sumário ativo
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param novoSeqp
	 */
	public void versionarAltaRecomendacao(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		List<MpmAltaRecomendacao> lista = this.getAltaRecomendacaoDAO().obterMpmAltaRecomendacao(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp, DominioSituacao.A);
		
		for (MpmAltaRecomendacao altaRecomendacao : lista) {
			
			MpmAltaRecomendacao novoAltaRecomendacao = new MpmAltaRecomendacao();
			novoAltaRecomendacao.setAltaSumario(altaSumario);
			novoAltaRecomendacao.setDescricao(altaRecomendacao.getDescricao());
			novoAltaRecomendacao.setDescricaoSistema(altaRecomendacao.getDescricaoSistema());
			novoAltaRecomendacao.setIndSituacao(altaRecomendacao.getIndSituacao());
			novoAltaRecomendacao.setPcuAtdSeq(altaRecomendacao.getPcuAtdSeq());
			novoAltaRecomendacao.setPcuSeq(altaRecomendacao.getPcuSeq());
			novoAltaRecomendacao.setPdtAtdSeq(altaRecomendacao.getPdtAtdSeq());
			novoAltaRecomendacao.setPdtSeq(altaRecomendacao.getPdtSeq());
			novoAltaRecomendacao.setPmdAtdSeq(altaRecomendacao.getPmdAtdSeq());
			novoAltaRecomendacao.setPmdSeq(altaRecomendacao.getPmdSeq());
			novoAltaRecomendacao.setServidorRecomendacaoAlta(altaRecomendacao.getServidorRecomendacaoAlta());
			this.getManterAltaRecomendacaoRN().inserirAltaRecomendacao(novoAltaRecomendacao);
			
		}
		
	}
	
	/**
	 * Retorna uma lista de alta recomendação 
	 * 
	 * @param id
	 * @return
	 */
	public List<AltaRecomendacaoVO> listarAltaRecomendacao(MpmAltaSumarioId id) throws ApplicationBusinessException {
		List<AltaRecomendacaoVO> listVO = new LinkedList<AltaRecomendacaoVO>();
		List<MpmAltaRecomendacao> list = this.getAltaRecomendacaoDAO().
			listarMpmAltaRecomendacao(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		
		for (MpmAltaRecomendacao mpmAltaRecomendacao : list) {
			
			if (StringUtils.isBlank(mpmAltaRecomendacao.getDescricaoSistema())) {
				
				AltaRecomendacaoVO vo = new AltaRecomendacaoVO();
				
				vo.setAltaSumario(mpmAltaRecomendacao.getAltaSumario());
				vo.setId(mpmAltaRecomendacao.getId());
				vo.setDescricao(mpmAltaRecomendacao.getDescricao());
				listVO.add(vo);
				
			}	
		
		}
		
		return listVO;
	}
	
	/**
	 * Método que exclui alta não cadastrada
	 * 
	 * @param vo
	 */
	public void removerAltaRecomendacao(AltaRecomendacaoVO vo) throws ApplicationBusinessException {
		MpmAltaRecomendacao mpmAltaRec = this.getAltaRecomendacaoDAO().obterPorChavePrimaria(vo.getId());
		
		this.getManterAltaRecomendacaoRN().removerAltaRecomendacao(mpmAltaRec);
	}
	
	/**
	 * Método que grava um 
	 * alta não cadastrada
	 * 
	 * @param vo
	 */
	public void gravarAltaRecomendacao(AltaRecomendacaoVO vo) throws ApplicationBusinessException {
		
		if(StringUtils.isEmpty(vo.getDescricao().trim())){
			//throw new IllegalArgumentException("Campo Descrição é Obrigatório");
			throw new ApplicationBusinessException(ManterAltaRecomendacaoONExceptionCode.DESCRICAO_OBRIGATORIA);
		}

        MpmAltaSumario sumario = vo.getAltaSumario();

		MpmAltaRecomendacao mpmAltaRec = new MpmAltaRecomendacao();
		MpmAltaRecomendacaoId id = new MpmAltaRecomendacaoId();
		
		id.setAsuApaAtdSeq(sumario.getId().getApaAtdSeq());
		id.setAsuApaSeq(sumario.getId().getApaSeq());
		id.setAsuSeqp(sumario.getId().getSeqp());
		
		mpmAltaRec.setId(id);
		mpmAltaRec.setAltaSumario(sumario);
		mpmAltaRec.setIndSituacao(sumario.getSituacao());
		
		vo.setDescricao(StringUtil.trim(vo.getDescricao()));
		mpmAltaRec.setDescricao(vo.getDescricao());

		this.getManterAltaRecomendacaoRN().inserirAltaRecomendacao(mpmAltaRec);
	}
	
	/**
	 * Método que atualiza uma 
	 * alta não cadastrada
	 * 
	 * @param vo
	 */
	public void atualizarAltaRecomendacao(AltaRecomendacaoVO vo) throws ApplicationBusinessException {
		if(StringUtils.isEmpty(vo.getDescricao().trim())){
			//throw new IllegalArgumentException("Campo Descrição é Obrigatório");
			throw new ApplicationBusinessException(ManterAltaRecomendacaoONExceptionCode.DESCRICAO_OBRIGATORIA);
		}
		
		MpmAltaRecomendacao mpmAltaRec = this.getAltaRecomendacaoDAO().obterPorChavePrimaria(vo.getId());
		vo.setDescricao(StringUtil.trim(vo.getDescricao()));
		mpmAltaRec.setDescricao(vo.getDescricao());
		
		this.getManterAltaRecomendacaoRN().atualizarAltaRecomendacao(mpmAltaRec);
	}
	
	/**
	 * Método que atualiza uma 
	 * alta não cadastrada através de um parâmetro do tipo do objeto AltaCadastrada
	 * 
	 * @param vo
	 */
	public void atualizarAltaRecomendacao(AltaCadastradaVO vo) throws ApplicationBusinessException {
		
		if(StringUtils.isEmpty(vo.getDescricao().trim())){
			//throw new IllegalArgumentException("Campo Descrição é Obrigatório");
			throw new ApplicationBusinessException(ManterAltaRecomendacaoONExceptionCode.DESCRICAO_OBRIGATORIA);
		}
		
		MpmAltaRecomendacao mpmAltaRec = this.getAltaRecomendacaoDAO().obterPorChavePrimaria(vo.getAltaRecomendacaoId());
		
		vo.setDescricao(StringUtil.trim(vo.getDescricao()));
		mpmAltaRec.setDescricao(vo.getDescricao());
		
		this.getManterAltaRecomendacaoRN().atualizarAltaRecomendacao(mpmAltaRec);
	}
	
	/**
	 * Método que exclui AltaRecomendacao
	 * 
	 * @param altaSumario
	 */
	public void removerAltaRecomendacao(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		
		List<MpmAltaRecomendacao> lista = this.getAltaRecomendacaoDAO().obterMpmAltaRecomendacao(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp(), null);
		
		for (MpmAltaRecomendacao altaRecomendacao : lista) {
			
			this.getManterAltaRecomendacaoRN().removerAltaRecomendacao(altaRecomendacao);
			
		}
		
	}
	
	/**
	 * Método que atualiza uma 
	 * alta de item de prescrição de um parâmetro do tipo do objeto ItemPrescricaoMedicaVO
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarAltaItemPrescricao(MpmAltaSumario altaSumario, ItemPrescricaoMedicaVO itemPrescricaoMedicaVO) throws ApplicationBusinessException{
		if(StringUtils.isEmpty(itemPrescricaoMedicaVO.getDescricao().trim())){
			//throw new IllegalArgumentException("Campo Descrição é Obrigatório");
			throw new ApplicationBusinessException(ManterAltaRecomendacaoONExceptionCode.DESCRICAO_OBRIGATORIA);
		}
		
		//Inativa o anterior
		MpmAltaRecomendacaoId id = new MpmAltaRecomendacaoId(altaSumario
				.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(),
				altaSumario.getId().getSeqp(), itemPrescricaoMedicaVO.getSeqp());
		MpmAltaRecomendacao altaRecomendacaoGravada = getAltaRecomendacaoDAO().obterPorChavePrimaria(id);
		
		if (altaRecomendacaoGravada.getDescricao().equalsIgnoreCase(itemPrescricaoMedicaVO.getDescricao())){
			throw new ApplicationBusinessException(ManterAltaRecomendacaoONExceptionCode.ALTA_RECOMENDACAO_INALTERADA);
		}
		
		altaRecomendacaoGravada.setIndSituacao(DominioSituacao.I);
		this.getManterAltaRecomendacaoRN().atualizarAltaRecomendacao(altaRecomendacaoGravada);		

		//Insere o novo item 
		MpmAltaRecomendacao altaRecomendacao = prepareAltaRecomendacao(altaSumario, itemPrescricaoMedicaVO);
		altaRecomendacao.setDescricao(itemPrescricaoMedicaVO.getDescricao());
		altaRecomendacao.setIndSituacao(DominioSituacao.A);
		this.getManterAltaRecomendacaoRN().inserirAltaRecomendacao(altaRecomendacao);
	
	}
	
	public void inativarAltaItemPrescricao(MpmAltaSumario altaSumario,
			ItemPrescricaoMedicaVO itemPrescricaoMedicaVO) throws ApplicationBusinessException {
		
		//Inativa o item
		MpmAltaRecomendacaoId id = new MpmAltaRecomendacaoId(altaSumario
				.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(),
				altaSumario.getId().getSeqp(), itemPrescricaoMedicaVO.getSeqp());
		MpmAltaRecomendacao altaRecomendacaoGravada = getAltaRecomendacaoDAO().obterPorChavePrimaria(id);
		altaRecomendacaoGravada.setIndSituacao(DominioSituacao.I);
		this.getManterAltaRecomendacaoRN().atualizarAltaRecomendacao(altaRecomendacaoGravada);
	}
	
	/**
	 * Atualiza os Itens Recomendados no Plano Pos Alta que deve ficar associados ao AltaSumario.<br>
	 * 
	 * @author ehgsilva
	 * 
	 * @param altaSumario
	 * @param listaItens
	 * @throws BaseException 
	 */
	public void gravarRecomendacaoPlanoPosAltaPrescricaoMedica(
			MpmAltaSumario altaSumario,
			List<ItemPrescricaoMedicaVO> listaItensInsert)
			throws BaseException {
		
		if (listaItensInsert.isEmpty()){
			throw new ApplicationBusinessException(ManterAltaRecomendacaoONExceptionCode.SELECIONAR_RECOMENDACAO_ALTA_GRAVAR);
		}
		
		for (ItemPrescricaoMedicaVO itemVo : listaItensInsert) {
//			RecomendacaoAltaItensPrescricaoHelper helper = 
					getRecomendacaoAltaItensPrescricaoHelper(altaSumario);
			MpmAltaRecomendacao altaRecomendacao = prepareAltaRecomendacao(altaSumario, itemVo);
			altaRecomendacao.setDescricao(itemVo.getDescricao());
			altaRecomendacao.setIndSituacao(DominioSituacao.A);
			this.getManterAltaRecomendacaoRN().inserirAltaRecomendacao(altaRecomendacao);
		}
		
	}
	
	
	/**
	 * Este método prepara um objeto do
	 * tipo MpmAltaRecomendacao da tela 
	 * de item alta prescricao, inicializando
	 * algumas variáveis e identificando qual
	 * o tipo de prescrição a ser persistido.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumario
	 * @param descricao
	 * @return
	 */
	private MpmAltaRecomendacao prepareAltaRecomendacao(MpmAltaSumario altaSumario, ItemPrescricaoMedicaVO itemVo){
		MpmAltaRecomendacao altaRecomendacao = new MpmAltaRecomendacao();
		
		MpmAltaRecomendacaoId id = new MpmAltaRecomendacaoId();
		id.setAsuApaAtdSeq(altaSumario.getId().getApaAtdSeq());
		id.setAsuApaSeq(altaSumario.getId().getApaSeq());
		id.setAsuSeqp(altaSumario.getId().getSeqp());
		
		altaRecomendacao.setAltaSumario(altaSumario);
		altaRecomendacao.setDescricao(itemVo.getDescricaoDB());
		altaRecomendacao.setDescricaoSistema(itemVo.getDescricaoDB());
		altaRecomendacao.setId(id);
		
		if(itemVo.getTipo().equals(PrescricaoMedicaTypes.CUIDADOS_MEDICOS)){
			altaRecomendacao.setPcuAtdSeq(itemVo.getAtendimentoSeq());
			altaRecomendacao.setPcuSeq(Integer.valueOf(itemVo.getItemSeq().intValue()));
		} else if(itemVo.getTipo().equals(PrescricaoMedicaTypes.DIETA)){
			altaRecomendacao.setPdtAtdSeq(itemVo.getAtendimentoSeq());
			altaRecomendacao.setPdtSeq(Integer.valueOf(itemVo.getItemSeq().intValue()));
		} else if(itemVo.getTipo().equals(PrescricaoMedicaTypes.MEDICAMENTO)){
			altaRecomendacao.setPmdAtdSeq(itemVo.getAtendimentoSeq());
			altaRecomendacao.setPmdSeq(Integer.valueOf(itemVo.getItemSeq().intValue()));
		}
		
		return altaRecomendacao;
	}
	
	/**
	 * Busca os itens da Ultima Prescricao Medica associado ao altaSumario.atendimento.<br>
	 * 
	 * @param altaSumario
	 * @return
	 * @throws BaseException 
	 */
	public List<ItemPrescricaoMedicaVO> buscaItensRecomendacaoPlanoPosAltaPrescricaoMedica(MpmAltaSumario altaSumario) throws BaseException {
		if (altaSumario == null || altaSumario.getAtendimento() == null || altaSumario.getAtendimento().getSeq() == null) {
			throw new IllegalArgumentException("Parametor invalido!!!!");
		}
		
		// Busca ultima Precricao Medica.
		VerificarPrescricaoON aVerificarPrescricaoON = getVerificarPrescricaoON();
		MpmPrescricaoMedica ultimaPrescricaoMedica = aVerificarPrescricaoON.obterUltimaPrescricaoAtendimento(
				altaSumario.getAtendimento().getSeq()
				, new Date()
				, new Date());
		
		// Busca os dados da prescricao medica. Apenas Dieta, Cuidados e Medicamentos.
		ManterPrescricaoMedicaON aManterPrescricaoMedicaON = getManterPrescricaoMedicaON();
		List<ItemPrescricaoMedicaVO> listItens = aManterPrescricaoMedicaON.buscarItensPrescricaoMedicaPlanoPosAlta(ultimaPrescricaoMedica.getId());
		
		for (ItemPrescricaoMedicaVO item : listItens) {
			item.setMarcado(Boolean.FALSE);
		}		
		
		return listItens;
	}
	
	/**
	 * Busca os itens da Ultima Prescricao Medica associado ao altaSumario.atendimento.<br>
	 * 
	 * @param altaSumario
	 * @return
	 * @throws BaseException 
	 */
	public List<ItemPrescricaoMedicaVO> buscaItensPrescricaoMedicaMarcado(MpmAltaSumario altaSumario) throws BaseException {
		if (altaSumario == null || altaSumario.getAtendimento() == null || altaSumario.getAtendimento().getSeq() == null) {
			throw new IllegalArgumentException("Parametor invalido!!!!");
		}
		
		// Cria o Helper com os Itens associados ao Alta Sumario.
		RecomendacaoAltaItensPrescricaoHelper helper = getRecomendacaoAltaItensPrescricaoHelper(altaSumario);
		
		
		List<MpmAltaRecomendacao> listaOriginal = getAltaRecomendacaoDAO().buscaItensAltaRecomendacaoPrescricaoMedica(altaSumario);
		List<ItemPrescricaoMedicaVO> listaVo = new LinkedList<ItemPrescricaoMedicaVO>();
		for(MpmAltaRecomendacao item : listaOriginal) {
			listaVo.add(helper.newItenPrescricaoMedicaVO(item));
		}
		
		return listaVo;
	}
	
		
	
	/**
	 * Buscar os itens de recomendacao plano pos alta associados ao AltaSumario<br>
	 * Cria o Helper.<br>
	 * 
	 * @param altaSumario
	 * @return
	 */
	private RecomendacaoAltaItensPrescricaoHelper getRecomendacaoAltaItensPrescricaoHelper(MpmAltaSumario altaSumario) {
		// Buscar os itens de recomendacao plano pos alta associados ao AltaSumario.
		ManterAltaRecomendacaoRN aManterAltaRecomendacaoRN = getManterAltaRecomendacaoRN();
		List<MpmAltaRecomendacao> listAltaRecomendacao = aManterAltaRecomendacaoRN.buscaItensAltaRecomendacaoPrescricaoMedica(altaSumario);
		
		// Helper para marcar os itens que estiverem associados ao AltaSumario.  
		return new RecomendacaoAltaItensPrescricaoHelper(listAltaRecomendacao);
	}
	
	private ManterPrescricaoMedicaON getManterPrescricaoMedicaON() {
		return manterPrescricaoMedicaON;
	}

	private VerificarPrescricaoON getVerificarPrescricaoON() {
		return verificarPrescricaoON;
	}
	
	protected MpmAltaRecomendacaoDAO getAltaRecomendacaoDAO() {
		return mpmAltaRecomendacaoDAO;
	}
	
	protected ManterAltaRecomendacaoRN getManterAltaRecomendacaoRN() {
		return manterAltaRecomendacaoRN;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void montarAltaNaoCadastradaRN(MpmAltaSumario pAltaSumario) throws ApplicationBusinessException {
		if(pAltaSumario.getPaciente().getMaePaciente() != null
                && pAltaSumario.getPaciente().getMaePaciente().getCodigo() != null
                && DominioOrigemAtendimento.N.equals(pAltaSumario.getAtendimento().getOrigem())){
			
			List<MpmAltaRecomendacao> list = this.getAltaRecomendacaoDAO().
					listarMpmAltaRecomendacao(pAltaSumario.getId().getApaAtdSeq(), pAltaSumario.getId().getApaSeq(), pAltaSumario.getId().getSeqp());
			boolean existeRecomendacoes = false;	
			for (MpmAltaRecomendacao mpmAltaRecomendacao : list) {
				if (StringUtils.isBlank(mpmAltaRecomendacao.getDescricaoSistema())
						&& isRecomendacaoNaoCadastradaRN(mpmAltaRecomendacao.getDescricao())) {
					existeRecomendacoes = true;
				}	
			}
			
			if(!existeRecomendacoes){
				inserirRecomendacaoRN(pAltaSumario,"1. Leite materno à vontade;");
				inserirRecomendacaoRN(pAltaSumario,"2. Teste do pezinho;");
				inserirRecomendacaoRN(pAltaSumario,"3. Álcool a 70% - Aplicar no COTO UMBILICAL 3x/dia, até 02 dias após a queda;");
				inserirRecomendacaoRN(pAltaSumario,"4. Retorno a Emergência se febre, \" amarelão\", recusa alimentar ou respiração rápida;");
				inserirRecomendacaoRN(pAltaSumario,"5. Ao pediatra em 7/10 dias para revisão;");
				inserirRecomendacaoRN(pAltaSumario,"6. Vacinações.");
			}
		}
	}

	private void inserirRecomendacaoRN(MpmAltaSumario pAltaSumario, String descricao)
			throws ApplicationBusinessException {
		MpmAltaRecomendacaoId id = new MpmAltaRecomendacaoId();
		id.setAsuApaAtdSeq(pAltaSumario.getId().getApaAtdSeq());
		id.setAsuApaSeq(pAltaSumario.getId().getApaSeq());
		id.setAsuSeqp(pAltaSumario.getId().getSeqp());
		MpmAltaRecomendacao mpmAltaRec = new MpmAltaRecomendacao();
		mpmAltaRec.setId(id);
		mpmAltaRec.setAltaSumario(pAltaSumario);
		mpmAltaRec.setIndSituacao(pAltaSumario.getSituacao());
		mpmAltaRec.setDescricao(descricao);
		this.getManterAltaRecomendacaoRN().inserirAltaRecomendacao(mpmAltaRec, false);
	}

	private boolean isRecomendacaoNaoCadastradaRN(String descricao) {
		return descricao.toLowerCase().startsWith("1. leite materno")
				|| descricao.toLowerCase().startsWith("2. teste do pezinho")
				|| descricao.toLowerCase().startsWith("3. álcool a 70")
				|| descricao.toLowerCase().startsWith("4. retorno a emergência se febre")
				|| descricao.toLowerCase().startsWith("5. ao pediatra em 7/10 dias")
				|| descricao.toLowerCase().startsWith("6. vacinações");
	}
}
