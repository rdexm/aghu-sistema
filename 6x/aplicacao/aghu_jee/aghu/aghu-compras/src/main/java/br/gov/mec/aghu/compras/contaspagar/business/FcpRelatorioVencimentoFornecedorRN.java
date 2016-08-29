package br.gov.mec.aghu.compras.contaspagar.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.compras.contaspagar.vo.DatasVencimentosFornecedorVO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class FcpRelatorioVencimentoFornecedorRN extends BaseBusiness {
		
	private static final long serialVersionUID = -5266964997238334317L;

		@Inject
		private ScoFornecedorDAO scoFornecedorDAO;
		
		@Inject 
		private AghParametrosDAO aghParametrosDAO;
		
		@Override
		protected Log getLogger() {
			return null;
		}
		
		/**
		 * Método reponsável por retornar a coleção com dados do relatório
		 * @param fornecedor dados referente ao fornecedor
		 * @return coleção com os dados do relatório
		 */
		public List<DatasVencimentosFornecedorVO> pesquisarDatasVencimentoFornecedor(Object fornecedor){
			VScoFornecedor vScoFornecedor = (VScoFornecedor) fornecedor;
			return scoFornecedorDAO.listarDatasVencimentoFornecedor(vScoFornecedor);
		}
		
		/**
		 * Método responsável por retornar objeto com as informações do hospital
		 * @param parametro
		 * @return
		 */
		public AghParametros pesquisarHospital(Object parametro) {
			return aghParametrosDAO.pesquisaHospital(parametro);
		}

		public ScoFornecedorDAO getScoFornecedorDAO() {
			return scoFornecedorDAO;
		}

		public void setScoFornecedorDAO(ScoFornecedorDAO scoFornecedorDAO) {
			this.scoFornecedorDAO = scoFornecedorDAO;
		}

		public AghParametrosDAO getAghParametrosDAO() {
			return aghParametrosDAO;
		}

		public void setAghParametrosDAO(AghParametrosDAO aghParametrosDAO) {
			this.aghParametrosDAO = aghParametrosDAO;
		}
			
	}