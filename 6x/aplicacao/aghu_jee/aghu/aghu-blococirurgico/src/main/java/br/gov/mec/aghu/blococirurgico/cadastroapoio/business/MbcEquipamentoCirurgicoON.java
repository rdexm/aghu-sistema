package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoCirgPorUnidDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoNotaSalaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoUtilCirgDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcEquipamentoCirgPorUnid;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcEquipamentoCirurgicoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcEquipamentoCirurgicoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcEquipamentoCirgPorUnidDAO mbcEquipamentoCirgPorUnidDAO;

	@Inject
	private MbcEquipamentoNotaSalaDAO mbcEquipamentoNotaSalaDAO;

	@Inject
	private MbcEquipamentoUtilCirgDAO mbcEquipamentoUtilCirgDAO;

	@Inject
	private MbcEquipamentoCirurgicoDAO mbcEquipamentoCirurgicoDAO;


	@EJB
	private MbcEquipamentoCirurgicoRN mbcEquipamentoCirurgicoRN;

	@EJB
	private MbcEquipamentoCirgPorUnidRN mbcEquipamentoCirgPorUnidRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6417825529219892638L;

	
	protected enum MbcEquipamentoCirurgicoONExceptionCode implements BusinessExceptionCode {
		MBC_00053,// 
		PENDENCIA_EQUIPAMENTO_UTIL_CIRG,//
		PENDENCIA_EQUIPAMENTO_NOTA_SALA,//
		PENDENCIA_EQUIPAMENTO_CIRG_POR_UNID,//
		;
	}
	
	
	
	public MbcEquipamentoCirurgico obterEquipamentoCirurgico(
			String descricao, Short codigo, DominioSituacao situacao ) {
		return this.getMbcEquipamentoCirurgicoDAO().obterEquipamentoCirurgico(
				descricao, codigo, situacao);
	}
	
	
	public List<MbcEquipamentoCirgPorUnid> listarEquipamentosCirgPorUnidade(MbcEquipamentoCirurgico equipamentoCirurgico) {
		return this.getMbcEquipamentoCirgPorUnidDAO().listarEquipamentoCirurgicoPorUnid(equipamentoCirurgico);
	}
	
	
	
	/**
	 * Atualiza um registro em<br>
	 * MBC_EQUIPAMENTO_CIRURGICOS.
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void atualizar(MbcEquipamentoCirurgico elemento) throws BaseException {
		this.verificarDescricaoCadastrada(elemento);
		this.getMbcEquipamentoCirurgicoRN().atualizar(elemento);
	}
	
	
	/**
	 * Insere um registro em<br>
	 * MBC_EQUIPAMENTO_CIRURGICOS.
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void inserir(MbcEquipamentoCirurgico elemento) throws BaseException {
		this.verificarDescricaoCadastrada(elemento);
		this.getMbcEquipamentoCirurgicoRN().inserir(elemento);
	}
	
	/**
	 * Remove um registro em<br>
	 * MBC_EQUIPAMENTO_CIRURGICOS.
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	protected void remover(MbcEquipamentoCirurgico elemento) throws BaseException {
		this.verificarPendencias(elemento);
		this.getMbcEquipamentoCirurgicoRN().remover(elemento);
	}
	
	/**
	 * CHK_MBC_EUU_UK1
	 * 
	 * Verifica se já existe <br> 
	 * equipamento cirúrgico com a mesma descrição.
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void verificarDescricaoCadastrada(MbcEquipamentoCirurgico elemento) throws BaseException {
		if(this.getMbcEquipamentoCirurgicoDAO()
				.obterEquipamentoCirurgico(elemento.getSeq(), elemento.getDescricao()) != null){
			throw new ApplicationBusinessException(
					MbcEquipamentoCirurgicoONExceptionCode.MBC_00053);
		}
	}
	
	
	/**
	 * CHK_MBC_EQUIPAMENTO_CIRUR
	 * 
	 * Verifica dependências em <br>
	 * MBC_EQUIPAMENTO_UTIL_CIRGS, <br> 
	 * MBC_EQUIPAMENTO_NOTA_SALAS <br> 
	 * e MBC_EQUIPAMENTO_CIRG_POR_UNIDS.
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void verificarPendencias(MbcEquipamentoCirurgico elemento) throws BaseException {
		if(!this.getMbcEquipamentoUtilCirgDAO()
				.listarEquipamentoUtilCirurgico(elemento).isEmpty()) {
			throw new ApplicationBusinessException(
					MbcEquipamentoCirurgicoONExceptionCode.PENDENCIA_EQUIPAMENTO_UTIL_CIRG);
		}
		
		if(!this.getMbcEquipamentoNotaSalaDAO()
				.listarEquipamentoNotaSala(elemento).isEmpty()) {
			throw new ApplicationBusinessException(
					MbcEquipamentoCirurgicoONExceptionCode.PENDENCIA_EQUIPAMENTO_NOTA_SALA);
		}
		
		if(!this.getMbcEquipamentoCirgPorUnidDAO()
				.listarEquipamentoCirurgicoPorUnid(elemento).isEmpty()) {
			throw new ApplicationBusinessException(
					MbcEquipamentoCirurgicoONExceptionCode.PENDENCIA_EQUIPAMENTO_CIRG_POR_UNID);
		}
	}
	
	
	public void atualizarEquipamentoCirurgicoPorUnid(
			MbcEquipamentoCirgPorUnid elemento) throws BaseException {
		this.getMbcEquipamentoCirgPorUnidRN().atualizar(elemento);
	}
	
	
	public void inserirEquipamentoCirurgicoPorUnid(
			MbcEquipamentoCirgPorUnid elemento) throws BaseException {
		this.getMbcEquipamentoCirgPorUnidRN().inserir(elemento);
	}
	
	
	public void removerEquipamentoCirurgicoPorUnid(MbcEquipamentoCirgPorUnid elemento) throws BaseException {
		this.getMbcEquipamentoCirgPorUnidRN().remover(elemento);
	}
	
	
	/** GET **/
	protected MbcEquipamentoCirurgicoDAO getMbcEquipamentoCirurgicoDAO() {
		return mbcEquipamentoCirurgicoDAO;
	}
	
	protected MbcEquipamentoUtilCirgDAO getMbcEquipamentoUtilCirgDAO() {
		return mbcEquipamentoUtilCirgDAO;
	}
	
	protected MbcEquipamentoNotaSalaDAO getMbcEquipamentoNotaSalaDAO() {
		return mbcEquipamentoNotaSalaDAO;
	}
	
	protected MbcEquipamentoCirgPorUnidDAO getMbcEquipamentoCirgPorUnidDAO() {
		return mbcEquipamentoCirgPorUnidDAO;
	}
	
	protected MbcEquipamentoCirurgicoRN getMbcEquipamentoCirurgicoRN() {
		return mbcEquipamentoCirurgicoRN;
	}
	
	protected MbcEquipamentoCirgPorUnidRN getMbcEquipamentoCirgPorUnidRN() {
		return mbcEquipamentoCirgPorUnidRN;
	}
}
